docker-compose -f ../docker-compose.yml up -d db
docker cp scripts slykbots-db:/root/
docker exec -it slykbots-db sh -c "sh /root/scripts/execute_scripts.sh"