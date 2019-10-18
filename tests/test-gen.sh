#!/bin/bash
read -p "How many mazes do you want to create? " n
dir="test_outputs"

if [ ! -d "$dir" ]; then
	mkdir "$dir"
fi

cd "$dir"

rm *.maze
rm *.png

echo "\n"
for i in `seq 1 $n`
do
    echo "Generating Maze #$i:"
	java -jar ../../build/mdlg.jar ../../examples/maze.config "maze${i}.maze"
	java -jar ../../build/mdlc.jar "maze${i}.maze"
	echo "\n"
done