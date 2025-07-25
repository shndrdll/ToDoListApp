/* Simple To-Do List Task Manager – Java Swing GUI
 * Author: Shandara Mae De Las Llagas
 * 
 * Description:
 * A beginner-friendly desktop task manager built with Java Swing. This application 
 * allows users to manage their personal to-do list.
 * 
 * Features:
 *  - Add new tasks
 *  - Remove selected tasks with confirmation
 *  - Mark/unmark tasks as completed (✔ Done)
 *  - Clear all tasks with confirmation
 *  - Live task counter display
 *  - Inline task editing (double-click to edit)
 *  - Dark mode toggle
 * 
 * Future Improvements:
 *  - Persistent storage (save/load tasks to file or database)
 *  - Sort or filter by completed status
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

        // top panel with task input and add button
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        inputPanel.setBackground(new Color(238, 242, 245));  // light blue-gray
        JTextField taskField = new JTextField(20);
        taskField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        taskField.setBackground(Color.WHITE);
        taskField.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        JButton addButton = new JButton("Add Task");

        // main action buttons
        JButton doneButton = new JButton("Mark as Done");
        JButton removeButton = new JButton("Remove Selected");
        JButton clearButton = new JButton("Clear All Tasks");

        // apply custom button colors
        styleButton(addButton, new Color(100, 149, 237));      // blue
        styleButton(doneButton, new Color(60, 179, 113));      // green
        styleButton(removeButton, new Color(205, 92, 92));     // red
        styleButton(clearButton, new Color(106, 90, 205));     // purple

        inputPanel.add(taskField);
        inputPanel.add(addButton);

        // middle task list
        taskListModel = new DefaultListModel<>();
        JList<String> taskList = new JList<>(taskListModel);
        taskList.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        taskList.setSelectionBackground(new Color(204, 229, 255));
        taskList.setBackground(new Color(247, 250, 255));  // soft bluish
        taskList.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        JScrollPane scrollPane = new JScrollPane(taskList);

        // bottom panel with buttons and counter
        // bottom panel with buttons, dark mode toggle, and task counter
        JPanel bottomPanel = new JPanel(new GridLayout(3, 1));
        bottomPanel.setBackground(new Color(238, 242, 245));

        // button row
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(new Color(238, 242, 245));
        buttonPanel.add(doneButton);
        buttonPanel.add(removeButton);
        buttonPanel.add(clearButton);

        // dark mode toggle
        JPanel togglePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        togglePanel.setBackground(new Color(238, 242, 245));
        JCheckBox darkModeToggle = new JCheckBox("Dark Mode");
        darkModeToggle.setFocusPainted(false);
        darkModeToggle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        darkModeToggle.setBackground(new Color(238, 242, 245));
        darkModeToggle.setForeground(Color.BLACK);
        togglePanel.add(darkModeToggle);

        // task counter label
        countLabel = new JLabel("Total tasks: 0", SwingConstants.CENTER);
        countLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));

        // Add all to bottom panel
        bottomPanel.add(buttonPanel);
        bottomPanel.add(togglePanel);
        bottomPanel.add(countLabel);

        // add task
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


        // press enter to add
        taskField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addButton.doClick();
            }
        });


        //remove selected task
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

        // mark task done
        doneButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int selectedIndex = taskList.getSelectedIndex();
                if (selectedIndex == -1) {
                    JOptionPane.showMessageDialog(frame, "Please select a task to mark as done.");
                    return;
                }

                String task = taskListModel.getElementAt(selectedIndex);
                if (task.endsWith("\u2714 Done")) {
                    taskListModel.setElementAt(removeDoneMark(task), selectedIndex);
                } else {
                    taskListModel.setElementAt(task + " \u2714 Done", selectedIndex);
                }
            }
        });

        // clear all tasks
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


        // double-click to edit task
        taskList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    int index = taskList.locationToIndex(evt.getPoint());
                    if (index != -1) {
                        String currentTask = taskListModel.getElementAt(index);
                        boolean isDone = currentTask.endsWith("\u2714 Done");
                        String plainTask = removeDoneMark(currentTask);

                        String edited = JOptionPane.showInputDialog(frame, "Edit task:", plainTask);
                        if (edited != null && !edited.trim().isEmpty()) {
                            String cleaned = removeDoneMark(edited.trim());
                            if (!cleaned.equalsIgnoreCase(plainTask) && isDuplicate(cleaned)) {
                                JOptionPane.showMessageDialog(frame, "This task already exists.");
                            } else {
                                if (isDone) {
                                    taskListModel.setElementAt(cleaned + " \u2714 Done", index);
                                } else {
                                    taskListModel.setElementAt(cleaned, index);
                                }
                            }
                        }
                    }
                }
            }
        });
        
     // dark mode
        darkModeToggle.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                boolean isDark = darkModeToggle.isSelected();
                Color bgColor = isDark ? new Color(34, 34, 34) : new Color(238, 242, 245);
                Color fgColor = isDark ? Color.WHITE : Color.BLACK;
                Color taskBg = isDark ? new Color(45, 45, 45) : new Color(247, 250, 255);
                Color taskFg = fgColor;

                // panels
                inputPanel.setBackground(bgColor);
                bottomPanel.setBackground(bgColor);
                buttonPanel.setBackground(bgColor);
                togglePanel.setBackground(bgColor); 

                // task list
                taskList.setBackground(taskBg);
                taskList.setForeground(taskFg);
                taskList.setSelectionBackground(isDark ? new Color(85, 85, 85) : new Color(204, 229, 255));

                // input field
                taskField.setBackground(isDark ? new Color(60, 60, 60) : Color.WHITE);
                taskField.setForeground(fgColor);

                // labels and toggles
                countLabel.setForeground(fgColor);
                countLabel.setBackground(bgColor); 
                darkModeToggle.setBackground(bgColor);
                darkModeToggle.setForeground(fgColor);
            }
        });



        // layout
        frame.add(inputPanel, BorderLayout.NORTH);
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(bottomPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }
    
    

    // check for duplicates
    private static boolean isDuplicate(String task) {
        for (int i = 0; i < taskListModel.size(); i++) {
            String existing = removeDoneMark(taskListModel.getElementAt(i));
            if (existing.equalsIgnoreCase(task)) {
                return true;
            }
        }
        return false;
    }

    // remove ✔ Done
    private static String removeDoneMark(String task) {
        if (task.endsWith("\u2714 Done")) {
            return task.substring(0, task.length() - "\u2714 Done".length()).trim();
        }
        return task.trim();
    }

    // update task count
    private static void updateTaskCount() {
        int count = taskListModel.getSize();
        countLabel.setText("Total tasks: " + count);
    }

    // style buttons
    private static void styleButton(JButton button, Color bgColor) {
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
    }
}
