#!/usr/bin/env bash

git clone https://github.com/kenzierocks/HNBT hnbt
cd hnbt
./gradlew install
cd ..
rm -rf hnbt
