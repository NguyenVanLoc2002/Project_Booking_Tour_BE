#!/bin/bash
# Kiểm tra biến môi trường RUN_ACTION_SERVER
if [ "$RUN_ACTION_SERVER" = "true" ]; then
  echo "Running Action Server..."
  rasa run actions --port 5055
else
  echo "Running Rasa Server..."
  rasa train
  rasa run --enable-api --cors "*"
fi