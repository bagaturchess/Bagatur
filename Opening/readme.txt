
This sub-project consists of both the Opening Book API of Bagatur engine and the utilities to operate over the opening books.

Opening Book API consists of:
1. bagaturchess.opening.api.OpeningBook - The API
2. bagaturchess.opening.api.OpeningBookFactory - The entry point factory for creating an OpeningBook instance.
In order to work the factory needs already generated opening book files for white and black sides.
For more details about the generation have a look at the readme file inside the OpeningGenerator sub-project.

Here are the utilities inside the run package (bagaturchess.opening.run):
1. BookTruncater - reduce the w.ob and b.ob openings in order to make the files' size ~ 1MB.
Normally you can remove the board states which are rarely observed (less than 3 times for example). 

2. TraverseBook - traverse the board states presented inside the opening books and calls callback methods of
specified bagaturchess.opening.api.traverser.OpeningsVisitor implementation.

3. ShortBookConverter - it is not used at all but the idea is to keep only the hashkeys of the board positions
which are presented in the Opening Book. It takes significantly less disk space (~ 20 times).

Have a nice usage ... and feel free to contribute.
