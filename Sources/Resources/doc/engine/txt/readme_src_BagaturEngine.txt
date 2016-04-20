

For the latest and greatest version of this readme file you can visit the SVN repository and check the Ants sub-project:
SVN repository https://bagaturchess.svn.sourceforge.net/svnroot/bagaturchess


As a chess programmer,
you want to have the sources of Bagatur chess engine as a part of the distribution so that you can easily compile and run them inside the Eclipse development environment.
This source distribution is exactly what you want. It is archived eclipse workspace with simple main method inside the EnginesRunner sub-project.


How to run the engine:
1. extract the archive in a new directory
2. import the existing projects in the Eclipse workspace
3. run the main class bagaturchess.engines.run.MTDSchedulerMain (it is inside the EnginesRunner sub-project)


How to build a distribution from the sources:
1. extract the archive in a new directory (<workspace>)
2. download http://sourceforge.net/projects/egtb-in-java/files/latest/download
3. get ./egtbprobe.dll from the archive and copy it to "<workspace>\EGTB" directory
3. get ./egtbprobe.jar from the archive and copy it to "<workspace>\EGTB\res" directory
4. copy w.ob and b.ob files (they are packed in the Bagatur engine distribution, inside 'dat' sub directory) to the 'Resources\bin\engine\ob' directory (it is inside your workspace, if it isn't presented than create it)
5. run the ant script Ants/engine/build_BagaturEngine_distro.xml from Ants/ directory either from eclipse or command line
6. the distribution archive file will be generated in the WorkDir directory


Have a nice usage ... and feel free to contribute http://sourceforge.net/projects/bagaturchess/develop

