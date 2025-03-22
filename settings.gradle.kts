rootProject.name = "tinder"
include("microservices:user-service")
findProject(":microservices:user-service")?.name = "user-service"
