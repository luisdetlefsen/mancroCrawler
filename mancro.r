


setwd("D:/mancro_archive")

properties <- read.csv("mancro.csv")

colnames(properties) <- c("mancroId", "precioVenta", "precioRenta", "ultimaEdicion", "visitas","habitaciones", "banos", "terreno", "construccion", "parqueo", "condicion", "zona","tipo","proposito","direccion","descripcion")
str(properties$precioRenta)


summary(properties$precioRenta)

#Graph 1
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