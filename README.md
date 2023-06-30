# UniversityDatabase

For this project, the table dataset for students was manually generated using the random number generator located in Google Sheets as well as an online website that generated the first and last names of 100 students. For the class names and credits, they are based off of real Rutgers classes as well as the credit amount.

In order to generate data for the tables, IsTaking and HasTaken, I populated each of them using a java code that roughly allocates around 25% for each class level for HasTaken for the credits amounts. For IsTaking, each student is randomly generated from one to five classes for the classes they are currently taking. Both random data in the table require the use of a random number generator in order to populate the tables.

The data from Google Sheets were uploaded by downloading the files as a csv file and uploading it via MySQLWorkbench (and were tested using queries). For the IsTaking and HasTaken tables, after running through the java code (which is connected via the JDBC driver), it populated the tables with the correct random data.
