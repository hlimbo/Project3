#!/bin/bash

## Restarts tomcat webserver
master_path=$(git rev-parse --show-toplevel)
$master_path/../bin/shutdown.sh
$master_path/../bin/startup.sh
