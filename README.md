# files2gramps
Java lib for files to pack as a export into [gramps database format](https://gramps-project.org/wiki/index.php?title=Gramps_XML).

[![Build Status](https://travis-ci.org/FunThomas424242/files2gramps.svg?branch=master)](https://travis-ci.org/FunThomas424242/files2gramps)
[![codecov](https://codecov.io/gh/FunThomas424242/files2gramps/branch/master/graph/badge.svg)](https://codecov.io/gh/FunThomas424242/files2gramps)
[![Download](https://api.bintray.com/packages/funthomas424242/funthomas424242-libs/files2gramps/images/download.svg) ](https://bintray.com/funthomas424242/funthomas424242-libs/files2gramps/_latestVersion)


# Usage
You need:
 
 * the prefix of tmpFolder as String
 * the gramps database file (in plain text xml format) as File
 * the target archive file as File 
 * the media folder as File
 
Then you can make a instance of GrampsExporter and call the createExportfile() method.
See the following code snippet how to use in a xtend project [ahnen.dsl](https://github.com/FunThomas424242/ahnen.dsl).

```
   
class AhnenGenerator extends AbstractGenerator {

	override void doGenerate(Resource resource, IFileSystemAccess2 fsa, IGeneratorContext context) {
		for (buch : resource.allContents.toIterable.filter(Familienbuch)) {
		    // generate gramps database
			fsa.generateFile(Helper.getGrampsDBFileName(buch),DataXMLGenerator.createGrampsDBContent(buch));
			var URI grampsDbfileURI=fsa.getURI(Helper.getGrampsDBFileName(buch));
			var File grampsDbfileFile = Helper.convertURI2File(buch,grampsDbfileURI);
			var File mediaFolderFile = Helper.getMediaFolderFile(buch,fsa);
			mediaFolderFile.mkdirs();
			var File grampsArchiveFileTmp = Files.createTempFile("gramps",null).toFile();
		    Helper.createTarGZ(grampsArchiveFileTmp, grampsDbfileFile ,mediaFolderFile);
		    var FileInputStream fIn = new FileInputStream(grampsArchiveFileTmp);
		    fsa.generateFile(Helper.getGrampsArchiveFileName(buch),fIn);
		    // generate docbook project
			fsa.generateFile(Helper.getPOMFileName(buch), POMGenerator.createPOMContent(buch))
			fsa.generateFile(Helper.getDbkFileName(buch, "book.dbk"), BookGenerator.createBookContent(fsa, buch))
		}
	}
}
     
```
# Developer info

## Build a release
(master will be only contains release versions)

1. create feature branch to develop a feature
9. at feature branch set the new snapshot version in pom.xml
9. develop the feature
9. check the CI phases for success
9. mvn -B release:prepare
9. mvn -B release:perform
9. merge the release tag e.g. 0.0.9-RELEASE into master
