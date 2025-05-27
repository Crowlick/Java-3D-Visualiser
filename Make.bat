if not exist bin mkdir bin

javac -d bin -cp ".\lib\jars\lwjgl.jar;.\lib\jars\lwjgl_util.jar;.\lib\jars\slick-util.jar;.\lib\jars\json-java.jar" src\toolBox\*.java src\entities\*.java src\renderEngine\*.java src\shaders\*.java src\engineTester\*.java src\mathTester\*.java

copy src\shaders\*.txt bin\shaders\