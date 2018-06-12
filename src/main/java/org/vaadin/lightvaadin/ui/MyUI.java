package org.vaadin.lightvaadin.ui;

import com.vaadin.annotations.Theme;
import com.vaadin.data.HasValue;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.*;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.vaadin.lightvaadin.repository.RepositoryMongoDB;
import org.vaadin.lightvaadin.service.ServiceRemoteData;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@Theme("valo")
public class MyUI extends UI {

    private static Logger log =  Logger.getLogger(MyUI.class.getName());

    private Label temperatureCurrent;
    private Label temperatureTomorrow;

    private Label ratesUSD;
    private Label ratesEURO;

    private ComboBox<String> city;

    private String counts;

    private RepositoryMongoDB repository = RepositoryMongoDB.getInstance();

    static {
        SLF4JBridgeHandler.install();
    }

    @Override
    protected void init(VaadinRequest request) {
        repository.increaseCounter();
        counts = repository.getCounter();

        Panel weather = initWeatherComponents();
        Panel exchangeRates = initRatesComponents();
        Panel numberOfVisitors = initNumbersOfVisitors();
        HorizontalLayout bottom = initBottom(request);
        VerticalLayout main = initMain(weather,exchangeRates,numberOfVisitors,bottom);
        initWindow(main);

    }

    private void initWindow(VerticalLayout main){
        HorizontalLayout window = new HorizontalLayout();
        window.setSizeFull();
        window.addComponents(main);
        window.setComponentAlignment(main,Alignment.MIDDLE_CENTER);
        setContent(window);
    }

    private VerticalLayout initMain(Panel weather, Panel exchangeRates, Panel numberOfVisitors, HorizontalLayout bottom) {
        VerticalLayout main = new VerticalLayout();
        Label labelCaption = new Label("Тестовое сетевое приложение");
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.addComponents(weather,exchangeRates,numberOfVisitors);
        main.addComponents(labelCaption,horizontalLayout,bottom);
        main.setComponentAlignment(labelCaption,Alignment.BOTTOM_CENTER);
        main.setComponentAlignment(horizontalLayout,Alignment.BOTTOM_CENTER);
        return main;
    }

    private void updateTemperature(Button.ClickEvent clickEvent) {
        String value = city.getValue();
        if(value==null){
            temperatureCurrent.setValue("Выберите город");
            temperatureTomorrow.setValue("");
            return;
        }
        String[] temperature = ServiceRemoteData.getTemperature(value);
        if(temperature==null){
            temperatureCurrent.setValue("Сервис недоступен");
        } else {
            temperatureCurrent.setValue("Температура текущая: " +temperature[0]);
            temperatureTomorrow.setValue("Температура на завтра: " +temperature[1]);
        }

    }

    private void updateRates(Button.ClickEvent clickEvent) {
        Map<String,String> rates = ServiceRemoteData.getExchangeRates();
        if(rates==null){
            ratesUSD.setValue("not connect");
        } else {
            ratesUSD.setValue("USD: "+rates.get("USD"));
            ratesEURO.setValue("EURO: "+rates.get("EUR"));
        }

    }
    private Panel initWeatherComponents(){
        Panel weather = new Panel();
        VerticalLayout verticalLayoutWeather = new VerticalLayout();
        verticalLayoutWeather.addComponent(new Label("Погода"));
        city = new ComboBox<>();
        initComboBoxCities(city);
        temperatureCurrent = new Label("Температура текущая: ");
        temperatureTomorrow = new Label("Температура на завтра: ");
        Button updateTemperature = new Button("Обновить");
        updateTemperature.addClickListener(this::updateTemperature);
        verticalLayoutWeather.addComponents(city,temperatureCurrent, temperatureTomorrow,updateTemperature);
        verticalLayoutWeather.setComponentAlignment(updateTemperature,Alignment.BOTTOM_CENTER);
        weather.setContent(verticalLayoutWeather);
        return weather;
    }

    private void initComboBoxCities(ComboBox city){
        List<String> cities = new ArrayList<>();
        cities.add("Novosibirsk");
        cities.add("Moscow");
        cities.add("Sankt-Peterburg");
        city.setDataProvider(new ListDataProvider<>(cities));
        city.addValueChangeListener((HasValue.ValueChangeListener<String>) valueChangeEvent ->
                city.setData(valueChangeEvent.getValue()));
    }

    private Panel initRatesComponents(){
        Panel exchangeRates = new Panel();
        exchangeRates.setSizeFull();
        VerticalLayout verticalLayoutExchangeRates = new VerticalLayout();
        verticalLayoutExchangeRates.setSizeFull();
        verticalLayoutExchangeRates.addComponent(new Label("Курсы валют"));
        ratesUSD = new Label("USD");
        ratesEURO = new Label("EUR");
        Button updateRates = new Button("Обновить");
        updateRates.addClickListener(this::updateRates);
        verticalLayoutExchangeRates.addComponents(ratesUSD,ratesEURO,updateRates);
        verticalLayoutExchangeRates.setComponentAlignment(updateRates,Alignment.BOTTOM_CENTER);
        exchangeRates.setContent(verticalLayoutExchangeRates);
        return exchangeRates;
    }

    private Panel initNumbersOfVisitors(){
        Panel numberOfVisitors = new Panel();
        numberOfVisitors.setSizeFull();
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setSizeFull();
        verticalLayout.addComponent(new Label("Счётчик посещений"));
        Label numbers = new Label();
        numbers.setValue(counts);
        numbers.addStyleName("h1");
        verticalLayout.addComponent(numbers);
        verticalLayout.setComponentAlignment(numbers,Alignment.MIDDLE_CENTER);
        numberOfVisitors.setContent(verticalLayout);
        return numberOfVisitors;
    }

    private HorizontalLayout initBottom(VaadinRequest request){
        HorizontalLayout bottom = new HorizontalLayout();
        bottom.setSizeFull();
        Label infoDate = new Label("Информация по состоянию на " + LocalDateTime.now().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT, FormatStyle.SHORT)));
        Label infoIp = new Label("Ваш ip: " + request.getRemoteAddr());
        bottom.addComponents(infoDate, infoIp);
        bottom.setComponentAlignment(infoDate,Alignment.BOTTOM_LEFT);
        bottom.setComponentAlignment(infoIp,Alignment.BOTTOM_RIGHT);
        return bottom;
    }

}
