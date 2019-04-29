# SJSUMap
## [CMPE 277] - Android App with map of SJSU with actual locations

***
Tested successfully on, 
Nexus 6 API 22 (EMU)
Samsung Galaxy Note 4
***

User Interface:
1. Map Screen : This will be the main screen of your application, and it will display the
provided map image.
2. Search Bar : A search bar to allow user search by building abbreviation.
3. Building Detail Screen : This ViewController will present more detailed information on
the selected building.
4. Street View Screen : follow the info here to integrate with Google’s Street View.
Features:
1. Map: Use the attached image as the map. (Don NOT use mapview or any other map
SDK/widget provided by Apple or Google!)
2. Building detail: A screen with the selected building detail should be presented when
user tap on a specific building on your map.
a. Building name
b. Address
c. Travel distance and time (walking or driving) from your current location to the
building
i. Google api should be used to retrieve the time estimation
1. https://developers.google.com/maps/documentation/distance-matrix/intro
2. Hint: Use longitude , latitude of the user as origin and provided
address in 2a as destination
d. A photo of the building
e. Button to trigger Street View
For simplicity, you can provide details for o nly the six building listed below.
3. Search: Allow user to search by building name(bolded below), and any found building
should be highlighted with a clear visual marker (pin or surrounding rectangle). The
markers go away only when the user clears the keywords in the search bar.
a. Building names(Case-insensitive) and addresses:
i. King Library : Dr. Martin Luther King, Jr. Library, 150 East San Fernando
Street, San Jose, CA 95112
ii. Engineering Building : San José State University Charles W. Davidson
College of Engineering, 1 Washington Square, San Jose, CA 95112
iii. Yoshihiro Uchida Hall : Yoshihiro Uchida Hall, San Jose, CA 95112
iv. Student Union : Student Union Building, San Jose, CA 95112
v. BBC : Boccardo Business Complex, San Jose, CA 95112
vi. South Parking Garage : San Jose State University South Garage, 330
South 7th Street, San Jose, CA 95112
4. Current location : the current location of the user must be shown on the map with a
small red circle.
5. Navigation between screens : the navigation betweens should be natural and intuitive.
For example, you should be able to do navigate back from Street View to the Building
Detail Screen, and from the Building Detail Screen to the Map Screen.
