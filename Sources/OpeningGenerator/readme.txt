
This is the proprietary Opening book's generator of Bagatur chess engine. 
It also serves as a good example of how to use BagaturPGNTool.

Here are the steps necessary to generate the Bagatur's opening book: 

1. First at all you need to have pgn files which will be the source for the opening book.
For example, you can download ~ 1 000 000 games from here http://www.chess.co.uk/twic/twic
(PGN format is the fourth column of the table)

2. Now you have to filter these games, for example by players' ELO and game result (white wins or black win).
For that purpose you can use the main class bagaturchess.tools.pgn.run.FilterPGNFiles in the 
PGNProcessor sub-project.

3. Generate w.ob and b.ob files, they will contain all the moves inside the filtered pgn games. For the generation you can use
bagaturchess.tools.opening.generator.run.OpeningsGenerator located inside the OpeningGenerator sub-project.

For more information about the Bagatur's Opening Book have a look at the readme file inside the Opening sub-project.

Have a nice usage ... and feel free to contribute.
