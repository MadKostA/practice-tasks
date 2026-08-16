### Создание docker образа и запуск контейнера
1) **Создаем jar**\
   mvn clean package
2) **Создаем образ**\
   docker build -t practice-tasks-app .  
3) **Запускаем контейнер**\
   docker run -p 8585:8585 practice-tasks-app
