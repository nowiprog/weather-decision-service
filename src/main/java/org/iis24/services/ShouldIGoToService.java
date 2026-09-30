package org.iis24.services;

import java.time.LocalDate;

/**
 * Servicio llamado "ShouldIGoToService" que responde si se debería visitar un lugar
 * en una fecha determinada (método "shouldIGoTo", devuelve un booleano), atendiendo
 * a criterios de preferencias de usuario en cuanto a probabilidad y cantidad de lluvia
 * pronosticada por un servicio de terceros (que implementa la interfaz IWeatherService).
 * Como los servicios que implementan IWeatherService necesitan unas coordenadas de GPS
 * para devolver un pronóstico, en lugar de una descripción del lugar como una cadena
 * de texto, ShouldIGoToService se apoya en otro servicio auxiliar que implementa la
 * interfaz IMapService, que devuelve las coordenadas GPS correspondientes al nombre de
 * un lugar, suministrado como una cadena de texto.
 */
public class ShouldIGoToService {

    private String placeDescription;

    private double rainProbabilityThreshold; // Umbral máximo de probabilidad de lluvia aceptable por usuario
    private double rainAmountThreshold; // Umbral máximo de cantidad de lluvia aceptable por usuario

    private IMapService myMapService;
    private IWeatherService myWeatherService;

    // Constructor checking instances are not NULL
    public ShouldIGoToService(IMapService ms, IWeatherService ws){
        if (ms==null) {
            throw (new InvalidServiceInstanceException("Invalid map service instance"));
        } else if (ws== null) {
            throw (new InvalidServiceInstanceException("Invalid weather service instance"));
        } else {
            this.myMapService = ms;
            this.myWeatherService = ws;
        }
    }

    public ShouldIGoToService(){

    }
    
    public void setRainProbabilityThreshold(double threshold){
        if (threshold>=0 && threshold<=100)
            this.rainProbabilityThreshold = threshold;
        else {
            throw (new InvalidRainProbabilityException());
        }
    }

    public double getRainProbabilityThreshold(){
        return this.rainProbabilityThreshold;
    }

    public void setRainAmountThreshold(double threshold){
        if (threshold >=0)
            this.rainAmountThreshold = threshold;
        else {
            throw (new InvalidRainAmountException());
        }
    }

    public double getRainAmountThreshold(){
        return this.rainAmountThreshold;
    }

    /**
     * Determina si se debería visitar un lugar en una fecha determinada,
     * dependiendo de las preferencias de usuario en cuanto a cantidad
     * y probabilidad de lluvia
     *
     * @param where cadena de texto que describe el lugar
     * @param when fecha (LocalDate) sobre la que se realiza la consulta
     * @return boolean que indica si se debería o no realizar la visita
     */
    public boolean shouldIGoTo (String where, LocalDate when){
        GPSCoordinates myCoords;
        double rainProb;
        double rainAmount;

        try {
            myCoords = this.myMapService.getCoordinates(where);
        } catch (Exception e){
            throw new InvalidMapServiceExecutionException("Map service received invalid input.");
        }

        if (when.isBefore(LocalDate.now())) {
            throw new RuntimeException("Date provided is in the past.");
        } else {
            rainProb = this.myWeatherService.rainProbability(myCoords, when);
            rainAmount = this.myWeatherService.totalAmountOfRain(myCoords, when);
        }

        if (rainProb!=0 && rainProb >= this.getRainProbabilityThreshold()
                && rainAmount >= this.getRainAmountThreshold())
            return false;
        else return true;
    }


}
