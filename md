#!/usr/bin/env bash
  java -version

	if [ ! -f "$2" ] #checks to see if filename parameter is in exist
	then
		echo "File $2 does not exist"

	elif [ "$1" == --addMolecule ] #checks if the options parameters exist
	then
			echo "Add... molecule from $2"
			java -jar out/artifacts/group9_jar/group9.jar $1 $2

  elif [ "$1" == --findMolecule ] #checks if the options parameters exist
	then
			echo "Search... molecule from $2"
      java -jar out/artifacts/group9_jar/group9.jar $1 $2

	else
		  echo "Operation $1 is not supported at this time."
	fi

