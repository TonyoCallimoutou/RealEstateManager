package com.tonyocallimoutou.realestatemanager.FAKE;

import com.tonyocallimoutou.realestatemanager.model.RealEstate;
import com.tonyocallimoutou.realestatemanager.model.User;

import java.util.Arrays;
import java.util.List;

public class FakeData {

    private static final User FAKE_USER = new User("100","FakeUserName","android.resource://com.tonyocallimoutou.realestatemanager/drawable/ic_no_image_available","fakeEmail");

    private static final User user1 = new User("1","Tonyo",null,"emailTonyo");
    private static final User user2 = new User("2","Jennifer",null,"emailJennifer");
    private static final User user3 = new User("3","Marc",null,"emailMarc");
    private static final User user4 =  new User("4","Alfred",null,"emailAlfred");
    private static final User user5 = new User("5","Jean",null,"emailJean");
    private static final User user6 = new User("6","Belle",null,"emailBelle");


    private static final List<User> FAKE_WORKMATES = Arrays.asList(
            user1,
            user2,
            user3,
            user4,
            user5,
            user6
    );


    private static final List<RealEstate> FAKE_REAL_ESTATE_LIST = Arrays.asList(
            new RealEstate(100000,FAKE_USER,"Fake Type",null,0,"Fake Description",120,1,1,1),
            new RealEstate(250000,FAKE_USER,"Fake Type 2",null,0,"Fake Description 2",360,5,2,3)
    );

    public static User getFakeCurrentUser() {
        return FAKE_USER;
    }
    public static List<User> getFakeWorkmates() {
        return FAKE_WORKMATES;
    }
    public static List<RealEstate> getFakeList() {
        return FAKE_REAL_ESTATE_LIST;
    }
}
