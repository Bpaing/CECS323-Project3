package enterprise;

import java.time.LocalTime;

import jakarta.persistence.*;

@Entity(name = "TIMESLOTS")
@Table(uniqueConstraints =
        @UniqueConstraint(columnNames = {"daysOfWeek", "startTime", "endTime"})
)
public class TimeSlot
{
    //Table attribute
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private int timeslotID;

    @Column(nullable = false)
    private byte daysOfWeek;

    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private LocalTime endTime;


    //Object methods
    public TimeSlot() {}

    public TimeSlot(byte daysOfWeek, LocalTime startTime, LocalTime endTime) {
        this.daysOfWeek = daysOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public int getTimeslotID() { return timeslotID; }

    public byte getDaysOfWeek() {
        return daysOfWeek;
    }

    public void setDaysOfWeek(byte daysOfWeek) {
        this.daysOfWeek = daysOfWeek;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }
}
