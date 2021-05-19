package com.example.todolist

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView:RecyclerView
    private lateinit var todoAdapter:TodoAdapter
    private lateinit var todoSQLiteOpenHelper: TodoSQLiteOpenHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        todoSQLiteOpenHelper = TodoSQLiteOpenHelper(this,"TodoDatabase.db",null,1)
        todoSQLiteOpenHelper.writableDatabase

        todoAdapter = TodoAdapter(mutableListOf(),todoSQLiteOpenHelper)

        recyclerView = findViewById(R.id.rvTodoItems)
        recyclerView.adapter = todoAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        todoAdapter.queryTodo()

        findViewById<Button>(R.id.btnAddTodo).setOnClickListener {
            val todoTitle = findViewById<EditText>(R.id.etTodoTitle).text.toString()
            if (todoTitle.isNotEmpty()){
                val todo = Todo(todoTitle)
                todoAdapter.addTodo(todo)
                findViewById<EditText>(R.id.etTodoTitle).text.clear()
            }
        }

        findViewById<Button>(R.id.btnDeleteTodo).setOnClickListener {
            todoAdapter.removeTodoDone()
        }
    }
}