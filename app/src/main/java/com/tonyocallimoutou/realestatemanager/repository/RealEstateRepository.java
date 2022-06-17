package com.tonyocallimoutou.realestatemanager.repository;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.preference.PreferenceManager;
import androidx.sqlite.db.SimpleSQLiteQuery;
import androidx.sqlite.db.SupportSQLiteQuery;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.OnProgressListener;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.tonyocallimoutou.realestatemanager.FAKE.FakeData;
import com.tonyocallimoutou.realestatemanager.R;
import com.tonyocallimoutou.realestatemanager.data.firebase.FirebaseDataRealEstate;
import com.tonyocallimoutou.realestatemanager.data.room.RealEstateDao;
import com.tonyocallimoutou.realestatemanager.data.room.UserDao;
import com.tonyocallimoutou.realestatemanager.model.Photo;
import com.tonyocallimoutou.realestatemanager.model.RealEstate;
import com.tonyocallimoutou.realestatemanager.model.User;
import com.tonyocallimoutou.realestatemanager.util.Filter;
import com.tonyocallimoutou.realestatemanager.util.UtilNotification;
import com.tonyocallimoutou.realestatemanager.util.Utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executor;

public class RealEstateRepository {

    private static volatile RealEstateRepository instance;

    private final FirebaseDataRealEstate firebaseDataRealEstate;
    private final RealEstateDao realEstateDao;
    private final Executor executor;
    private static boolean isConnected;

    private List<Filter> filters = new ArrayList<>();

    private MediatorLiveData<List<RealEstate>> mediatorLiveData = new MediatorLiveData<>();
    private SupportSQLiteQuery query;
    private final Context context;


    private RealEstateRepository(Context context, RealEstateDao realEstateDao, Executor executor) {
        firebaseDataRealEstate = FirebaseDataRealEstate.getInstance(context);
        this.realEstateDao = realEstateDao;
        this.executor = executor;
        this.context = context;
    }

    public static RealEstateRepository getInstance(Context context,RealEstateDao realEstateDao, Executor executor) {
        synchronized (RealEstateRepository.class) {
            if (instance == null) {
                instance = new RealEstateRepository(context, realEstateDao, executor);
            }
            return instance;
        }
    }

    public void createRealEstate(RealEstate realEstate) {
        realEstate.setSync(false);

        executor.execute(() -> {
            realEstateDao.createRealEstate(realEstate);
        });

        if (isConnected) {
            UtilNotification.createNotification(instance.context, realEstate);
            firebaseDataRealEstate.savePicture(context,realEstate, realEstateDao, executor);
        }
    }

    public void saveAsDraft(RealEstate realEstate) {
        realEstate.setDraft(true);
        executor.execute(()-> {
            realEstateDao.createRealEstate(realEstate);
        });
    }

    public void deleteDraft(RealEstate realEstate) {
        executor.execute(() -> {
            realEstateDao.deleteDraft(realEstate.getId());
        });
    }

    public void setFilterList(List<Filter> list) {
        filters = list;
        initListWithFilter();
    }

    public LiveData<List<RealEstate>> getListWithFilter() {
        initListWithFilter();
        return mediatorLiveData;
    }

    private void initListWithFilter() {


        StringBuilder strQuery = new StringBuilder("SELECT * FROM RealEstate ");


        for (int i = 0; i < filters.size(); i++) {
            if (i==0) {
                strQuery.append("WHERE ");
            }
            else {
                strQuery.append(" AND ");
            }
            strQuery.append(filters.get(i).getQueryStr());
        }

        query = new SimpleSQLiteQuery(strQuery.toString());

        Log.d("TAG", "initListWithFilter: " + strQuery);

        mediatorLiveData.addSource(realEstateDao.getRealEstatesLiveData(query), new Observer<List<RealEstate>>() {
                    @Override
                    public void onChanged(List<RealEstate> realEstates) {
                        mediatorLiveData.setValue(realEstates);
                    }
                });
    }

    public RealEstate soldRealEstate(RealEstate realEstate) {
        realEstate.setSync(false);

        if (realEstate.isSold()) {
            realEstate.setSold(false);
            realEstate.setSoldDate(null);
        }
        else {
            realEstate.setSold(true);
            realEstate.setSoldDate(Utils.getTodayDate());
        }

        if (isConnected) {
            firebaseDataRealEstate.editRealEstate(realEstate);
        }

        else {
            executor.execute(() -> {
                realEstateDao.createRealEstate(realEstate);
            });
        }

        return realEstate;

    }

    public void setMyRealEstates(User user) {

        if (isConnected) {
            firebaseDataRealEstate.setMyRealEstates(user);
        }
        else {
            executor.execute(() -> {
                List<RealEstate> realEstates = realEstateDao.getRealEstates();

                for (RealEstate realEstate : realEstates) {
                    if (realEstate.getUser().getUid().equals(user.getUid())) {
                        realEstate.setSync(false);
                        realEstate.setUser(user);
                        realEstateDao.createRealEstate(realEstate);
                    }
                }
            });
        }
    }

    public static void ConnectionChanged(boolean result) {
        if (result && instance!= null && !isConnected) {
            Log.d("TAG", "ConnectionChanged: ");
            syncFirebase();
        }
        isConnected = result;
    }

    private static void syncFirebase() {
        instance.firebaseDataRealEstate.setListRealEstates(instance.realEstateDao, instance.executor);

        instance.executor.execute(()-> {
            List<RealEstate> realEstates = instance.realEstateDao.getNotSyncRealEstates();

            for (RealEstate realEstate : realEstates) {
                UtilNotification.createNotification(instance.context, realEstate);
                if (realEstate.getProgressSync() == 100) {
                    instance.firebaseDataRealEstate.editRealEstate(realEstate);
                } else {
                    instance.firebaseDataRealEstate.savePicture(instance.context, realEstate, instance.realEstateDao, instance.executor);
                }
            }
        });
    }

}
