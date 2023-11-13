#!/usr/bin/env bash


SAVED="`pwd`"
cd "`dirname \"$PRG\"`/.." >&-
APP_HOME="`pwd -P`"
cd "$SAVED" >&-

gfsh --e "connect" --e "shutdown --include-locators=yes"

rm -rf ${APP_HOME}/data
