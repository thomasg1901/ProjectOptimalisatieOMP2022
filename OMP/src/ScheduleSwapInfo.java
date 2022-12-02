public class ScheduleSwapInfo {
    private Job[] schedule;
    private int index1Swapped;
    private int index2Swapped;

    public ScheduleSwapInfo(Job[] schedule, int index1Swapped, int index2Swapped) {
        this.schedule = schedule;
        this.index1Swapped = index1Swapped;
        this.index2Swapped = index2Swapped;
    }

    public Job[] getSchedule() {
        return schedule;
    }

    public int getIndex1Swapped() {
        return index1Swapped;
    }

    public int getIndex2Swapped() {
        return index2Swapped;
    }
}
