package goodpath;

public class Report {
    private  double latitude;
    private  double longitude;
    private  Type problemType;

    public Report(){

    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setProblemType(Type problemType) {
        this.problemType = problemType;
    }

    public void setLatitude(double latitude) {

        this.latitude = latitude;
    }

    public Report(Type problemType, double latitude, double longitude){
        this.problemType = problemType;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public double getLatitude(){
        return this.latitude;
    }

    public double getLongitude(){
        return this.longitude;
    }

    public Type getType(){
        return this.problemType;
    }

    @Override
    public String toString() {
        return "Report{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                ", problemType='" + problemType + '\'' +
                '}';
    }

    public enum Type{HARASSMENT, ACCESSIBILITY}
}
