package com.tonyocallimoutou.realestatemanager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

import android.app.Activity;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.tonyocallimoutou.realestatemanager.FAKE.FakeData;
import com.tonyocallimoutou.realestatemanager.model.Photo;
import com.tonyocallimoutou.realestatemanager.model.RealEstate;
import com.tonyocallimoutou.realestatemanager.model.RealEstateLocation;
import com.tonyocallimoutou.realestatemanager.model.User;
import com.tonyocallimoutou.realestatemanager.repository.RealEstateRepository;
import com.tonyocallimoutou.realestatemanager.repository.UserRepository;
import com.tonyocallimoutou.realestatemanager.viewmodel.ViewModelRealEstate;
import com.tonyocallimoutou.realestatemanager.viewmodel.ViewModelUser;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ViewModelTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private RealEstateRepository realEstateRepository;
    @Mock
    private ViewModelUser viewModelUser;
    @Mock
    private ViewModelRealEstate viewModelRealEstate;
    @Mock
    private Activity activity;

    private List<User> fakeWorkmates = new ArrayList<>(FakeData.getFakeWorkmates());
    private List<RealEstate> fakeRealEstates = new ArrayList<>(FakeData.getFakeList());
    private User currentUser = FakeData.getFakeCurrentUser();

    @Rule
    public TestRule rule = new InstantTaskExecutorRule();

    @Before
    public void setup() {
        openMocks(this);

        when(userRepository.getCurrentUser()).thenReturn(currentUser);

        initAnswerUser();
        initAnswerRealEstate();

        viewModelUser = ViewModelUser.getInstance(userRepository);
        viewModelRealEstate = ViewModelRealEstate.getInstance(realEstateRepository,userRepository);
    }

    private void initAnswerUser() {
        doAnswer(new Answer() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                fakeWorkmates.add(currentUser);
                return null;
            }
        }).when(userRepository).createUser(any(Activity.class), any(ViewModelUser.class));

        doAnswer(new Answer() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                fakeWorkmates.remove(currentUser);
                return null;
            }
        }).when(userRepository).deleteUser();

        doAnswer(new Answer() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                String name = (String) args[0];
                currentUser.setUsername(name);
                return null;
            }
        }).when(userRepository).setNameOfCurrentUser(any(String.class));

        doAnswer(new Answer() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                String picture = (String) args[0];
                currentUser.setUrlPicture(picture);
                return null;
            }
        }).when(userRepository).setCurrentUserPicture(any(String.class));

        doAnswer(new Answer() {
            @Override
            public LiveData<User> answer(InvocationOnMock invocation) throws Throwable {
                MutableLiveData<User> liveData = new MutableLiveData<>();
                liveData.setValue(currentUser);
                return liveData;
            }
        }).when(userRepository).getCurrentUserLiveData();

        doAnswer(new Answer() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                RealEstate realEstate = (RealEstate) args[0];
                currentUser.addRealEstateToMyList(realEstate);
                return null;
            }
        }).when(userRepository).createRealEstate(any(RealEstate.class));

    }

    private void initAnswerRealEstate() {
        doAnswer(new Answer() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                RealEstate realEstate = (RealEstate) args[0];
                fakeRealEstates.add(realEstate);
                return null;
            }
        }).when(realEstateRepository).createRealEstate(any(RealEstate.class));

        doAnswer(new Answer() {
            @Override
            public LiveData<List<RealEstate>> answer(InvocationOnMock invocation) throws Throwable {
                MutableLiveData<List<RealEstate>> liveData = new MutableLiveData<>();
                liveData.setValue(fakeRealEstates);
                return liveData;
            }
        }).when(realEstateRepository).getListWithFilter();

        doAnswer(new Answer() {
            @Override
            public RealEstate answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                RealEstate realEstate = (RealEstate) args[0];
                for (RealEstate item : fakeRealEstates) {
                    if (item.getId().equals(realEstate.getId())) {
                        if (item.isSold()) {
                            item.setSold(false);
                            item.setSoldDate(null);
                        }
                        else {
                            item.setSold(true);
                            item.setSoldDate(new Date());
                        }

                        return item;
                    }
                }
                return null;
            }
        }).when(realEstateRepository).soldRealEstate(any(RealEstate.class));
    }

    @Test
    public void createUser() {
        int workmatesSize = fakeWorkmates.size();
        for (User user : fakeWorkmates) {
            assertNotEquals(user.getEmail(), currentUser.getEmail());
        }
        viewModelUser.createUser(activity);
        assertEquals(workmatesSize+1, fakeWorkmates.size());
        assertTrue(fakeWorkmates.contains(currentUser));
    }

    @Test
    public void deleteUser() {
        viewModelUser.createUser(activity);
        assertTrue(fakeWorkmates.contains(currentUser));

        viewModelUser.deleteUser();

        assertFalse(fakeWorkmates.contains(currentUser));
        for (User user : fakeWorkmates) {
            assertNotEquals(user.getEmail(), currentUser.getEmail());
        }
    }

    @Test
    public void setName() {
        assertEquals("FakeUserName", currentUser.getUsername());

        viewModelUser.setNameOfCurrentUser("newNameTest");
        assertEquals("newNameTest", currentUser.getUsername());
    }

    @Test
    public void getLiveDataCurrentUser() {
        User user = viewModelUser.getCurrentUserLiveData().getValue();


        assertEquals(currentUser.getUsername(), user.getUsername());
        assertEquals(currentUser.getUrlPicture(), user.getUrlPicture());
        assertEquals(currentUser.getEmail(), user.getEmail());
    }

    @Test
    public void setCurrentUserPicture() {
        String currentPicture = viewModelUser.getCurrentUserLiveData().getValue().getUrlPicture();

        assertEquals(currentPicture, currentUser.getUrlPicture());

        String newPicture = "New Picture";
        viewModelUser.setCurrentUserPicture(newPicture);

        assertEquals(newPicture, viewModelUser.getCurrentUserLiveData().getValue().getUrlPicture());
    }

    @Test
    public void createRealEstate() {
        List<Photo> photos = FakeData.getFakePhotos();
        RealEstateLocation location = new RealEstateLocation("test","name",1.0,2.0,"test");
        RealEstate newRealEstate = new RealEstate(100000,currentUser,1,photos,0,"Fake Description",120,1,1,1,location);

        viewModelRealEstate.createRealEstate(newRealEstate);

        List<String> listRealEstateOfUser = viewModelUser.getCurrentUserLiveData().getValue().getMyRealEstateId();

        assertTrue(listRealEstateOfUser.contains(newRealEstate.getId()));
        assertTrue(fakeRealEstates.contains(newRealEstate));
    }

    @Test
    public void getAllRealEstate() {

        List<RealEstate> list = viewModelRealEstate.getFilterListLiveData().getValue();

        assertEquals(fakeRealEstates,list);
    }

    @Test
    public void soldRealEstate() {
        RealEstate realEstate = fakeRealEstates.get(0);

        assertFalse(realEstate.isSold());
        assertNull(realEstate.getSoldDate());

        RealEstate newRealEstate = viewModelRealEstate.soldRealEstate(realEstate);

        assertTrue(realEstate.isSold());
        assertNotNull(realEstate.getSoldDate());
        assertEquals(realEstate,newRealEstate);


    }

}
