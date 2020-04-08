#!/bin/bash

echo $1

if [ -d "$1" ]; then
  # create additional figure folder
  mkdir "${1}/Extension_figures"

  cp "${1}/ape.configuration" "${1}/apeExt.configuration"
  cp "${1}/sat_solutions.txt" "${1}/sat_solutions_Ext.txt"
  printf '{\r\n\t"constraints":[]\r\n}' >"${1}/constraintsEmpty.json"

  # edit new configuration file
  sed -i 's/constraints.json/constraintsEmpty.json/g' "${1}/apeExt.configuration"
  sed -i 's/Figures/Extension_figures/g' "${1}/apeExt.configuration"

fi
