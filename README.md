This example for explain how to customize Keycloak for add a new custom IDP Broker. (Here a missing OAuth2 implementation)

See *OAuthIdentityProvider#extractIdentityFromProfile* for modify attribute to retrieve from response 


# Build sources
```
mvn clean install
```

# Docker installation

```
docker build -t custom-keycloak:4.0.0.Final . 
```

```
docker export custom-keycloak > custom-keycloak-latest.tar
docker load < custom-keycloak-latest.tar 
```


## Install Custom Keycloak with Docker on EC2

```
sudo yum update -y
sudo yum install -y docker
sudo service docker start
sudo usermod -a -G docker ec2-user

docker network create keycloak-network
docker run --restart=always -d --name postgres --net keycloak-network -e POSTGRES_DB=keycloak -e POSTGRES_USER=keycloak -e POSTGRES_PASSWORD=password postgres

docker run --restart=always -p8080:8080 -d --name keycloak -e PROXY_ADDRESS_FORWARDING=true --net keycloak-network custom-keycloak:4.0.0.Final
```

