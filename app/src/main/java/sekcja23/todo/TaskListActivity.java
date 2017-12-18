package sekcja23.todo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import sekcja23.todo.R;
import sekcja23.todo.Models.TaskModel;
import sekcja23.todo.Utils.RequestCode;

public class TaskListActivity extends AppCompatActivity {

    private ListView listView ;
    private ArrayAdapter<TaskModel> listViewData ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_list);
        listView = findViewById(R.id.taskListView);

        getDataToTaskList();
    }


    public void goToAddNewTask(View view) {
        Intent nextScreen = new Intent(getApplicationContext(), AddNewTaskActivity.class);
        startActivityForResult(nextScreen, 100);
    }

    protected void getDataToTaskList() {
        // listViewData = new ArrayAdapter<TaskModel>(this, R.layout.row, carL);
        listView.setAdapter(listViewData);
    }


}
