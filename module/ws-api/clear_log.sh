#!/bin/bash

#Clear serialize file
rm `find ./logs -iname "*.log"`
rm `find ./logs -iname "*.log.*"`
rm `find ./logs -iname "*.txt"`
rm `find ./logs -iname "*.out"`
