


setwd("D:/mancro_archive")

properties <- read.csv("mancro.csv")

colnames(properties) <- c("mancroId", "precioVenta", "precioRenta", "ultimaEdicion", "visitas","habitaciones", "banos", "terreno", "construccion", "parqueo", "condicion", "zona","tipo","proposito","direccion","descripcion")

dim(properties)
str(properties$precioRenta)

#Levels of a factor
levels(properties$proposito)

#Filter properties by sale/rent
venta <- properties[properties$proposito=="venta", ]

alquiler <- properties[properties$proposito=="alquiler", ]

summary(properties$precioRenta)


library(dplyr)
 alquiler<-dplyr::filter(properties, grepl("alquiler", proposito))
> venta<-dplyr::filter(properties, grepl("venta", proposito))


#No tomar en cuenta propiedades que tienen el alquiler mayor a 4 veces la mediana.
 median(alquiler$precioRenta, na.rm = TRUE)
alquilerLimpio <- alquiler[alquiler$precioRenta <= 4*median(alquiler$precioRenta,na.rm=TRUE),]
 ggplot(alquilerLimpio, aes(alquilerLimpio$zona, alquilerLimpio$precioRenta)) + geom_point()


#Graph 1
 library(ggplot2)
ggplot(properties, aes(properties$zona, properties$precioVenta)) + geom_point()


#Graph 2
library(plotly)

xconf <- list(
  autotick = FALSE,
  ticks = "outside",
  tick0 = 0,
  dtick = 1,
  ticklen = 5,
  tickwidth = 2,
  tickcolor = toRGB("blue"),
  title = "Zona"
)

yconf <- list(
  autotick = TRUE,
  ticks = "outside",
  ticklen = 5,
  tickwidth = 2,
  tickcolor = toRGB("blue"),
  title = "Precio venta (USD)"
)
plot_ly(properties, x = ~zona, y = ~precioVenta , type="scatter", mode="markers" , marker=list(color="purple" , size=20 , opacity=0.5)  ) %>%layout(xaxis = xconf, yaxis = yconf)


#Graph 3
library(tidyverse)
library(plotly)

# Scatterplot (usar mancroId en vez de visitas para poder mostrar el id)
p=ggplot(properties, aes(x=zona, y=precioVenta, color=visitas, shape=condicion)) + geom_point(size=10, alpha=0.1)
ggplotly(p)