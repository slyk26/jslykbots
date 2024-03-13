#!/usr/bin/env sh

files="ls /root/scripts/*.sql";

for file in $files
do
  if [ -f "$file" ]; then
   /usr/bin/psql -U markov -d markov -f "$file";
  fi
done