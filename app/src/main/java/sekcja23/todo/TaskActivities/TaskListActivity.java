package sekcja23.todo.TaskActivities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import sekcja23.todo.R;

public class TaskListActivity extends AppCompatActivity {

    private ListView listView ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_list);
        listView = findViewById(R.id.taskListView);

        getDataToTaskList();
        listView.setOnItemClickListener((AdapterView<?> parent, View view, int position, long id) -> {
            goToTaskDetails((int) id);
        });
    }


    public void goToAddNewTask(View view) {
        Intent nextScreen = new Intent(getApplicationContext(), AddNewTaskActivity.class);
        startActivityForResult(nextScreen, 100);
    }

    public void goToTaskDetails(int taskId) {
        Toast.makeText(getApplicationContext(), "[LOG] - Go to task details!", Toast.LENGTH_SHORT).show();
        Intent nextScreen = new Intent(getApplicationContext(), TaskDetailsActivity.class);
        nextScreen.putExtra("TaskId", taskId);
        startActivityForResult(nextScreen, 100);
    }

    protected void getDataToTaskList() {

    }


}
