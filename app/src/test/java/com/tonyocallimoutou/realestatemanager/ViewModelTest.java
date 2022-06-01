package com.tonyocallimoutou.realestatemanager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

import android.app.Activity;
import android.content.Context;
import android.location.Location;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.tonyocallimoutou.realestatemanager.FAKE.FakeData;
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
    private Context context;
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

        viewModelUser = new ViewModelUser(userRepository);
        viewModelRealEstate = new ViewModelRealEstate(realEstateRepository,userRepository);
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
        }).when(userRepository).deleteUser(any(Context.class));

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
            public Void answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                MutableLiveData<User> liveData = (MutableLiveData<User>) args[0];
                liveData.setValue(currentUser);
                return null;
            }
        }).when(userRepository).setCurrentUserLivedata(any(MutableLiveData.class));

        doAnswer(new Answer() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                RealEstate realEstate = (RealEstate) args[0];
                currentUser.addRealEstateToMyList(realEstate);
                return null;
            }
        }).when(userRepository).createRealEstate(any(RealEstate.class));

        doAnswer(new Answer() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                MutableLiveData<List<User>> liveData = (MutableLiveData<List<User>>) args[0];
                liveData.setValue(fakeWorkmates);
                return null;
            }
        }).when(userRepository).getAllUser(any(MutableLiveData.class));

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
            public Void answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                MutableLiveData<List<RealEstate>> liveData = (MutableLiveData<List<RealEstate>>) args[0];
                liveData.setValue(fakeRealEstates);
                return null;
            }
        }).when(realEstateRepository).getAllRealEstates(any(MutableLiveData.class));
    }


    @Test
    public void getCurrentUser() {

        User user = viewModelUser.getCurrentUser();

        assertEquals(currentUser.getUid(), user.getUid());
        assertEquals(currentUser.getUsername(), user.getUsername());
        assertEquals(currentUser.getUrlPicture(), user.getUrlPicture());
        assertEquals(currentUser.getEmail(), user.getEmail());
    }

    @Test
    public void createUser() {
        int workmatesSize = fakeWorkmates.size();
        for (User user : fakeWorkmates) {
            assertNotEquals(user.getUid(), currentUser.getUid());
        }
        viewModelUser.createUser(activity);
        assertEquals(workmatesSize+1, fakeWorkmates.size());
        assertTrue(fakeWorkmates.contains(currentUser));
    }

    @Test
    public void deleteUser() {
        viewModelUser.createUser(activity);
        assertTrue(fakeWorkmates.contains(currentUser));

        viewModelUser.deleteUser(context);

        assertFalse(fakeWorkmates.contains(currentUser));
        for (User user : fakeWorkmates) {
            assertNotEquals(user.getUid(), currentUser.getUid());
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
        viewModelUser.setCurrentUserLiveData();
        User user = viewModelUser.getCurrentUserLiveData().getValue();

        assertEquals(currentUser.getUid(), user.getUid());
        assertEquals(currentUser.getUsername(), user.getUsername());
        assertEquals(currentUser.getUrlPicture(), user.getUrlPicture());
        assertEquals(currentUser.getEmail(), user.getEmail());
    }

    @Test
    public void setCurrentUserPicture() {
        String currentPicture = viewModelUser.getCurrentUser().getUrlPicture();

        assertEquals(currentPicture, currentUser.getUrlPicture());

        String newPicture = "New Picture";
        viewModelUser.setCurrentUserPicture(newPicture);

        assertEquals(newPicture, viewModelUser.getCurrentUser().getUrlPicture());
    }

    @Test
    public void getAllUser() {

        viewModelUser.setListUser();

        List<User> users = viewModelUser.getAllUser().getValue();

        assertEquals(fakeWorkmates, users);
    }

    @Test
    public void createRealEstate() {

        RealEstateLocation location = new RealEstateLocation("test","name",1.0,2.0,"test");

        RealEstate newRealEstate = new RealEstate(100000,currentUser,"Fake Type",null,0,"Fake Description",120,1,1,1,location);

        viewModelRealEstate.createRealEstate(newRealEstate);

        List<String> listRealEstateOfUser = viewModelUser.getCurrentUser().getMyRealEstateId();

        assertTrue(listRealEstateOfUser.contains(newRealEstate.getId()));
        assertTrue(fakeRealEstates.contains(newRealEstate));
    }

    @Test
    public void getAllRealEstate() {

        viewModelRealEstate.setListRealEstate();

        List<RealEstate> list = viewModelRealEstate.getALlRealEstateLiveData().getValue();

        assertEquals(fakeRealEstates,list);
    }

}
