#!/usr/bin/env bash
set -euo pipefail

# cria os topics na subida — o auto-create do broker não define partições/replicação como eu quero
BROKER="${KAFKA_BROKER:-kafka:29092}"

kafka-topics --bootstrap-server "$BROKER" --create --if-not-exists \
  --topic payments.events --partitions 3 --replication-factor 1

kafka-topics --bootstrap-server "$BROKER" --create --if-not-exists \
  --topic payments.events.dlq --partitions 1 --replication-factor 1

echo "topics criados."
