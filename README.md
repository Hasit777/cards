CardGame - ECM2414
requirements JDK 21
             Junit 4.13.2
             Hamcrest Core 1.3
The source files consist of:
 Card.java
 CardDeck.java
 CardGame.java
 PackReader.java
 Player.java
 stateGame.java
And the test classes are:
 CardTest.java
 CardDeckTest.java
 CardGameTest.java
 PackReaderTest.java
 PlayerTest.java
 StateGameTest.java

The game is already compiled in cards.jar
to run it go to the file location and use the following command
  java -jar cards.jar
but if you want to compile it your self you will need to run the following command
  javac -d out Card.java CardDeck.java CardGame.java PackReader.java Player.java stateGame.java
and to run the file you can use
  java -cp out CardGame

the program will ask for the number of players it requires a positive interger
if there is no positive integer then it will ask again.
it will then ask for the file location of the pack include the .txt file extention on the end of it
it is expecting 8n where n is the number of people if there is not enough it will ask again for another file.

each player log includes its intial hand its completed draw and discard and the players hand
after tge end of a game there may be decks which have one too few or too many
this is due to us ending the game mid thread.
you can check the outputs in files named "playerX_output.txt"
you can also check the final output for the decks at "deckX.txt"

A game is limited to 30 seconds, incase there cannot be a victory.

you can compile the tests by running the command
  javac -cp "lib\junit-4.13.2.jar;lib\hamcrest-core-1.3.jar" -d out Card.java CardDeck.java CardGame.java PackReader.java Player.java stateGame.java CardTest.java CardDeckTest.java CardGameTest.java PackReaderTest.java PlayerTest.java StateGameTest.java
you can then run said code via:
  java -cp "out;lib\junit-4.13.2.jar;lib\hamcrest-core-1.3.jar" org.junit.runner.JUnitCore CardTest CardDeckTest CardGameTest PackReaderTest PlayerTest StateGameTest
You can see what is tested in the documentation but for a small overview
CardTest test valid values, negative values and text output
CardDeckTest tests fifo order, draw, discard, locking and concurrent access
PackReaderTest tests files being empty or invalid, reading and handiling files.
StateGameTest tests intial and finsihed games states
PlayerTest tests discard order, four card hands stopping and card conservation
CardGameTest test invalid inputs, dealing, winning, complete game concurrency
