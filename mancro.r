
setwd("D:/mancro_archive")

properties <- read.csv("mancro.csv")

colnames(properties) <- c("mancroId", "precioVenta", "precioRenta", "ultimaEdicion", "visitas","habitaciones", "banos", "terreno", "construccion", "parqueo", "condicion", "zona","tipo","proposito","direccion","descripcion")
str(properties$precioRenta)


summary(properties$precioRenta)

