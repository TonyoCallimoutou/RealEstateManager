package com.tonyocallimoutou.realestatemanager.FAKE;

import com.tonyocallimoutou.realestatemanager.model.Photo;
import com.tonyocallimoutou.realestatemanager.model.RealEstate;
import com.tonyocallimoutou.realestatemanager.model.RealEstateLocation;
import com.tonyocallimoutou.realestatemanager.model.User;

import java.util.Arrays;
import java.util.List;

public class FakeData {

    private static final User FAKE_USER = new User("FakeUserName","android.resource://com.tonyocallimoutou.realestatemanager/drawable/ic_no_image_available","fakeEmail");

    private static final User user1 = new User("Tonyo",null,"emailTonyo");
    private static final User user2 = new User("Jennifer",null,"emailJennifer");
    private static final User user3 = new User("Marc",null,"emailMarc");
    private static final User user4 =  new User("Alfred",null,"emailAlfred");
    private static final User user5 = new User("Jean",null,"emailJean");
    private static final User user6 = new User("Belle",null,"emailBelle");


    private static final List<User> FAKE_WORKMATES = Arrays.asList(
            user1,
            user2,
            user3,
            user4,
            user5,
            user6
    );

    private static final List<RealEstateLocation> FAKE_LOCATION = Arrays.asList(
            new RealEstateLocation("1","Name Location 1",1.0,2.0,"address location 1"),
            new RealEstateLocation("2","Name Location 2",2.0,3.0,"address location 2")
    );

    private static final List<Photo> FAKE_PHOTOS = Arrays.asList(
            new Photo("android.resource://com.tonyocallimoutou.realestatemanager/drawable/ic_no_image_available",null)
    );

    private static final List<RealEstate> FAKE_REAL_ESTATE_LIST = Arrays.asList(
            new RealEstate(100000,FAKE_USER,0,FAKE_PHOTOS,0,"Fake Description",120,1,1,1,FAKE_LOCATION.get(0)),
            new RealEstate(250000,FAKE_USER,1,FAKE_PHOTOS,0,"Fake Description 2",360,5,2,3,FAKE_LOCATION.get(1))
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
    public static List<Photo> getFakePhotos() {
        return FAKE_PHOTOS;
    }
}
