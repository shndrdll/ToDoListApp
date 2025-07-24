/* Simple To-Do List Task Manager using Java Swing
 * Author: Shandara Mae De Las Llagas
 * Description: A beginner-friendly desktop application to manage tasks with basic features.
 * Features:
 *  - Add and remove tasks
 *  - Mark tasks as done (✔ Done)
 *  - Clear all tasks with confirmation
 *  - Live task counter
 *  - (Optional) Save/Load functionality can be added in the future
 */

package taskManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ToDoList {

    private static DefaultListModel<String> taskListModel;
    private static JLabel countLabel;

    public static void main(String[] args) {
        JFrame frame = new JFrame("My To-Do List App");
        frame.setSize(400, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // top area where to type and add tasks
        JPanel inputPanel = new JPanel(new FlowLayout());
        JTextField taskField = new JTextField(20);
        JButton addButton = new JButton("Add Task");
        inputPanel.add(taskField);
        inputPanel.add(addButton);

        // middle area that shows the task list
        taskListModel = new DefaultListModel<>();
        JList<String> taskList = new JList<>(taskListModel);
        JScrollPane scrollPane = new JScrollPane(taskList);

        // bottom part with buttons and task counter
        JPanel bottomPanel = new JPanel(new GridLayout(2, 1));
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton doneButton = new JButton("Mark as Done");
        JButton removeButton = new JButton("Remove Selected");
        JButton clearButton = new JButton("Clear All Tasks");

        buttonPanel.add(doneButton);
        buttonPanel.add(removeButton);
        buttonPanel.add(clearButton);

        countLabel = new JLabel("Total tasks: 0", SwingConstants.CENTER);
        bottomPanel.add(buttonPanel);
        bottomPanel.add(countLabel);

        // when add button is clicked
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String task = taskField.getText().trim();
                String cleanedTask = removeDoneMark(task);

                if (task.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Please enter a task.");
                } else if (isDuplicate(cleanedTask)) {
                    JOptionPane.showMessageDialog(frame, "This task already exists.");
                } else {
                    taskListModel.addElement(task);
                    taskField.setText("");
                    updateTaskCount();
                }
                taskField.requestFocusInWindow();
            }
        });

        // press enter key to add task
        taskField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addButton.doClick();
            }
        });

        // remove selected task from the list
        removeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int selectedIndex = taskList.getSelectedIndex();
                if (selectedIndex == -1) {
                    JOptionPane.showMessageDialog(frame, "Please select a task to remove.");
                } else {
                    int confirm = JOptionPane.showConfirmDialog(
                        frame,
                        "Are you sure you want to remove this task?",
                        "Confirm Remove",
                        JOptionPane.YES_NO_OPTION
                    );
                    if (confirm == JOptionPane.YES_OPTION) {
                        taskListModel.remove(selectedIndex);
                        updateTaskCount();
                        taskField.requestFocusInWindow();
                    }
                }
            }
        });

        // mark selected task as done or undo it
        doneButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int selectedIndex = taskList.getSelectedIndex();
                if (selectedIndex == -1) {
                    JOptionPane.showMessageDialog(frame, "Please select a task to mark as done.");
                    return;
                }

                String task = taskListModel.getElementAt(selectedIndex);
                if (task.endsWith("✔ Done")) {
                    String undoneTask = removeDoneMark(task);
                    taskListModel.setElementAt(undoneTask, selectedIndex);
                } else {
                    taskListModel.setElementAt(task + " ✔ Done", selectedIndex);
                }
            }
        });

        // clear all tasks from the list
        clearButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (taskListModel.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "No tasks to clear.");
                    return;
                }

                int confirm = JOptionPane.showConfirmDialog(
                    frame,
                    "Are you sure you want to clear all tasks?",
                    "Confirm Clear All",
                    JOptionPane.YES_NO_OPTION
                );

                if (confirm == JOptionPane.YES_OPTION) {
                    taskListModel.clear();
                    updateTaskCount();
                    taskField.requestFocusInWindow();
                }
            }
        });

        // double-click a task to edit it
        taskList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    int index = taskList.locationToIndex(evt.getPoint());
                    if (index != -1) {
                        String currentTask = taskListModel.getElementAt(index);
                        boolean isDone = currentTask.endsWith("✔ Done");
                        String plainTask = removeDoneMark(currentTask);

                        String edited = JOptionPane.showInputDialog(frame, "Edit task:", plainTask);
                        if (edited != null && !edited.trim().isEmpty()) {
                            String cleaned = removeDoneMark(edited.trim());

                            if (!cleaned.equalsIgnoreCase(plainTask) && isDuplicate(cleaned)) {
                                JOptionPane.showMessageDialog(frame, "This task already exists.");
                            } else {
                                if (isDone) {
                                    taskListModel.setElementAt(cleaned + " ✔ Done", index);
                                } else {
                                    taskListModel.setElementAt(cleaned, index);
                                }
                            }
                        }
                    }
                }
            }
        });

        // add everything to the frame
        frame.add(inputPanel, BorderLayout.NORTH);
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(bottomPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    // check if task is already in the list (ignores ✔ done)
    private static boolean isDuplicate(String task) {
        for (int i = 0; i < taskListModel.size(); i++) {
            String existing = removeDoneMark(taskListModel.getElementAt(i));
            if (existing.equalsIgnoreCase(task)) {
                return true;
            }
        }
        return false;
    }

    // remove ✔ done from task if it's there
    private static String removeDoneMark(String task) {
        if (task.endsWith("✔ Done")) {
            return task.substring(0, task.length() - "✔ Done".length()).trim();
        }
        return task.trim();
    }

    // show how many tasks there are
    private static void updateTaskCount() {
        int count = taskListModel.getSize();
        countLabel.setText("Total tasks: " + count);
    }
}
