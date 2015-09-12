# MixinAnnoProcessor

## How to run UseMixinannoprocessor
1. Download the project *UseMixinAnnoProcessor* and import into Eclipse.
2. Download the jar file *MixinAnnoprocessor/MixinProcessor.jar*
3. In Eclipse, go to project _Properties_ settings:
    * add MixinProcessor.jar to build path by: Java Build Path -> Libraries ->
      Add External JARs. 
    * set MixinProcessor.jar as the annotation processor by:
     Java Compiler -> Annotation Processing -> Factory Path -> Add External
     JARs.
4. Build
5. Run test.Main.java

## How to run UseMixinLombok
1. In terminal, browse to directory *MixinAnnoprocessor/MixinLombok*
2. `$ant eclipse`
3. `$ant`
4. Quit eclipse
5. Run (by double clicking) the generated jar
   *MixinAnnoprocessor/MixinLombok/dist/lombok.jar* 
4. Now start eclipse and import the jar file to project *UseMixinLombok*
