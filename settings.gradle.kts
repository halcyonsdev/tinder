rootProject.name = "tinder"
include("microservices:user-service")
findProject(":microservices:user-service")?.name = "user-service"
include("common:redis-cache")
findProject(":common:redis-cache")?.name = "redis-cache"
include("microservices:auth-service")
findProject(":microservices:auth-service")?.name = "auth-service"
include("common:jwt-core")
findProject(":common:jwt-core")?.name = "jwt-core"
include("common:exception-core")
findProject(":common:exception-core")?.name = "exception-core"
include("microservices:config-service")
findProject(":microservices:config-service")?.name = "config-service"
include("microservices:swipe-service")
findProject(":microservices:swipe-service")?.name = "swipe-service"
