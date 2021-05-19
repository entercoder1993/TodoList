package com.example.todolist

import android.content.ContentValues
import android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TodoAdapter(
        private val todos: MutableList<Todo>,
        private val todoSQLiteOpenHelper: TodoSQLiteOpenHelper
) : RecyclerView.Adapter<TodoAdapter.TodoViewHolder>() {

    class TodoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTodoTitle: TextView = itemView.findViewById(R.id.tvTodoTitle)
        val cbDone: CheckBox = itemView.findViewById(R.id.cbDone)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        return TodoViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.item_todo, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return todos.size
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        val curTodo = todos[position]
        holder.tvTodoTitle.text = curTodo.title
        holder.cbDone.isChecked = curTodo.isCheck
        toggleStrikeThrouht(holder.tvTodoTitle, curTodo.isCheck)
        holder.cbDone.setOnCheckedChangeListener { _, isChecked ->
            toggleStrikeThrouht(holder.tvTodoTitle, isChecked)
            curTodo.isCheck = !curTodo.isCheck
            updateTodo(curTodo)
        }
    }

    private fun toggleStrikeThrouht(tvTodoTitle: TextView, isChecked: Boolean) {
        if (isChecked) {
            tvTodoTitle.paintFlags = tvTodoTitle.paintFlags or STRIKE_THRU_TEXT_FLAG
        } else {
            tvTodoTitle.paintFlags = tvTodoTitle.paintFlags and STRIKE_THRU_TEXT_FLAG.inv()
        }
    }

    fun addTodo(todo: Todo) {
        todos.add(todo)
        var writableDatabase = todoSQLiteOpenHelper.writableDatabase
        writableDatabase.insert("Todo", null, ContentValues().apply {
            put("todo_title", todo.title)
        })
        notifyItemInserted(todos.size - 1)
    }

    fun removeTodoDone() {
        todos.removeAll { todo ->
            todo.isCheck
        }
        var writableDatabase = todoSQLiteOpenHelper.writableDatabase
        writableDatabase.delete("Todo", "todo_ischecked = ?", arrayOf("1"))
        notifyDataSetChanged()
    }

    fun updateTodo(todo: Todo) {
        var writableDatabase = todoSQLiteOpenHelper.writableDatabase
        writableDatabase.update("Todo", ContentValues().apply {
            put("todo_ischecked", 1)
        }, "todo_title = ?", arrayOf(todo.title))
    }

    fun queryTodo() {
        var writableDatabase = todoSQLiteOpenHelper.writableDatabase
        writableDatabase.query("Todo", null, null, null, null, null, null).apply {
            if (this.moveToFirst()) {
                do {
                    val todoTitle = getString(getColumnIndex("todo_title"))
                    val todoIsChecked = getInt(getColumnIndex("todo_ischecked"))
                    var isChecked: Boolean = false
                    if (todoIsChecked != 0) {
                        isChecked = true
                    }
                    val todo: Todo = Todo(todoTitle, isChecked)
                    todos.add(todo)
                } while (moveToNext())
                notifyDataSetChanged()
            }
        }
    }
}