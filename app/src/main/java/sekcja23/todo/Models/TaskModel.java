package sekcja23.todo.Models;


import java.util.ArrayList;
import java.util.List;

public class TaskModel {
    private String taskTitle;
    private String comment;

    private PositionOnMap positionOnMap;

    private List<Remainder> remainderList;
    private List<FileResource> resourceList;

    public TaskModel(String taskTitle, String comment) {
        this.taskTitle = taskTitle;
        this.comment = comment;

        this.remainderList = new ArrayList<Remainder>();
        this.resourceList = new ArrayList<FileResource>();
    }


    public void addRemainder(Remainder remainder) {
        this.remainderList.add(remainder);
    }

    public void addResourceList(FileResource resource) {
        this.resourceList.add(resource);
    }

    public void setPositionOnMap(PositionOnMap positionOnMap) {
        this.positionOnMap = positionOnMap;
    }
}
