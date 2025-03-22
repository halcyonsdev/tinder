rootProject.name = "tinder"
include("microservices:user-service")
findProject(":microservices:user-service")?.name = "user-service"
include("common:redis-cache")
findProject(":common:redis-cache")?.name = "redis-cache"
