CLASS?=KNN
run: build
	java -cp ./bin cs107KNN.$(CLASS)

build:
	javac src/cs107KNN/*.java

