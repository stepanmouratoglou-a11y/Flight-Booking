
import java.util.Scanner;

public class Flight {

    private String flightNumber;
    private String date;
    private String from;
    private String to;
    private String Airline;
    private int numOfCustomers=0;

    public void makeNew(String flightNumber,String date,String from,String to,String Airline){
      Flight flight=new Flight(flightNumber,date,from,to,Airline);
      System.out.println();
      
    }
    
    public Flight(String flightNumber,String date,String from,
    String to,String Airline){
        this.flightNumber = flightNumber;
        this.date = date;
        this.from = from;
        this.to = to;
        this.Airline = Airline;
        this.numOfCustomers = 0;
    }

   
    public boolean addPassengers(int count){
        if(count <= 0) return false;
        if(this.numOfCustomers + count <= 250){
            this.numOfCustomers += count;
            return true;
        } else {
           
            return false;
        }
    }

    public int getNumOfCustomers(){
        return this.numOfCustomers;
    }

    public String getflightNumber(){
        return flightNumber;
    }

     public String getdate(){
        return date;
    }

     public String getfrom(){
        return from;
    }

     public String getto(){
        return to;
    }

     public String getAirline(){
        return Airline;
    }

    
    public void changeDate(){

        Scanner scanner=new Scanner(System.in);
        this.date=scanner.nextLine();
    }



   

    @Override
    public String toString(){
        return String.format(
            "Flight Number: %s | Airline: %s | From: %s -> To: %s | Date: %s | Passengers: %d",
            flightNumber == null ? "N/A" : flightNumber,
            Airline == null ? "N/A" : Airline,
            from == null ? "N/A" : from,
            to == null ? "N/A" : to,
            date == null ? "N/A" : date,
            numOfCustomers
        );
    }
    

}