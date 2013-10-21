package com.licensetokil.atypistcalendar.tasksmanager;

import com.licensetokil.atypistcalendar.parser.*;
import java.io.*;
import java.util.*;

public class TasksManager {

	private LocalAction action;
	private static ArrayList<Schedule> schedule = new ArrayList<Schedule>();
	private static ArrayList<Deadline> deadline = new ArrayList<Deadline>();
	private static ArrayList<Todo> todo = new ArrayList<Todo>();

	private static ArrayList<Schedule> sch = new ArrayList<Schedule>();
	private static ArrayList<Deadline> dl = new ArrayList<Deadline>();
	private static ArrayList<Todo> toDo = new ArrayList<Todo>();

	private static ArrayList<Task> deletedTasks = new ArrayList<Task>();

	private static ArrayList<Task> markUndoList;
	private static Task updateOriginalTask;

	private static LocalAction lastAction;

	private static Hashtable<Integer, Task> table = new Hashtable<Integer, Task>();

	private static int counter = 0;
	private static int count = 1;

	private static File file = new File("ATC");

	public void setAction(LocalAction action) {
		this.action = action;
	}

	public LocalAction getAction() {
		return action;
	}

	public ArrayList<Schedule> getSchedule() {
		return schedule;
	}

	public ArrayList<Deadline> getDeadline() {
		return deadline;
	}

	public ArrayList<Todo> getTodo() {
		return todo;
	}

	private static Task classify(AddAction action) {
		Task t;
		if (action.getStartTime() == null) {
			if (action.getEndTime() == null) {
				t = new Todo(++counter, action.getDescription(),
						action.getPlace(), "undone");
			} else {
				t = new Deadline(++counter, action.getEndTime(),
						action.getDescription(), action.getPlace(), "undone");
			}
		} else {
			t = new Schedule(++counter, action.getStartTime(),
					action.getEndTime(), action.getDescription(),
					action.getPlace());
		}

		return t;
	}

	private static String add(Task t) {

		try {
			BufferedWriter w = new BufferedWriter(new FileWriter(file));
			String output = "";

			if (t.getTaskType().equals("schedule")) {
				schedule.add((Schedule) t);
				output = "Added:\n" + "Event: " + t.getDescription()
						+ "\nStarting Time: "
						+ ((Schedule) t).getStartTime().getTime()
						+ "\nEnding Time: "
						+ ((Schedule) t).getEndTime().getTime() + "\n";
				
			}

			else if (t.getTaskType().equals("deadline")) {
				deadline.add((Deadline) t);
				output = "Added\n" + "Event: "
						+ ((Deadline) t).getDescription() + "\nDue by: "
						+ ((Deadline) t).getEndTime().getTime() + "\n";
			}

			else if (t.getTaskType().equals("todo")) {
				todo.add((Todo) t);
				output = "Added\n" + "Event: " + t.getDescription() + "\n";
			}

			w.write(t.toString());
			w.close();
			
			if (!t.getPlace().equals("")) {
				output = output + "Place: " + t.getPlace() + "\n";
			}

			return output;

		} catch (Exception e) {
			return "Add was unsuccessful. Please try again";
		}

	}

	private static void addUndo() {
		Task t = classify((AddAction) lastAction);
		schedule.remove(t);
		deadline.remove(t);
		todo.remove(t);
	}

	private static String display(DisplayAction ac) {
		sch.clear();
		dl.clear();
		toDo.clear();
		table.clear();

		String output = new String("");
		count = 1;

		switch (ac.getDescription()) {

		case "schedules":
			for (Schedule s : schedule) {
				if (s.getStartTime().after(ac.getStartTime())
						&& s.getEndTime().before(ac.getEndTime())) {
					sch.add(s);
				}
			}
			break;
		case "deadlines":
			for (Deadline d : deadline) {
				if (ac.getStatus().equals("")) {
					if (d.getEndTime().after(ac.getStartTime())
							&& d.getEndTime().before(ac.getEndTime())) {
						dl.add(d);
					}
				} else {
					if (d.getEndTime().after(ac.getStartTime())
							&& d.getEndTime().before(ac.getEndTime())
							&& d.getStatus().equals(ac.getStatus())) {
						dl.add(d);
					}
				}
			}
			break;
		case "todos":
			for (Todo td : todo) {
				if (td.getStatus().equals("")) {
					toDo.add(td);
				} else {
					if (td.getStatus().equals(ac.getStatus())) {
						toDo.add(td);
					}
				}
			}
			break;
		case "all":
			for (Schedule s : schedule) {
				if (s.getStartTime().after(ac.getStartTime())
						&& s.getEndTime().before(ac.getEndTime())) {
					sch.add(s);
				}
			}
			for (Deadline d : deadline) {
				if (d.getEndTime().after(ac.getStartTime())
						&& d.getEndTime().before(ac.getEndTime())) {
					dl.add(d);
				}
			}
			if (ac.getEndTime().get(Calendar.YEAR) == 2099) {
				for (Todo td : todo) {
					toDo.add(td);
				}
			}
			break;

		case "":
			for (Deadline d : deadline) {
				if (d.getEndTime().after(ac.getStartTime())
						&& d.getEndTime().before(ac.getEndTime())
						&& d.getStatus().equals(ac.getStatus())) {
					dl.add(d);
				}
			}
			if (ac.getEndTime().get(Calendar.YEAR) == 2099) {
				for (Todo td : todo) {
					if (td.getStatus().equals(ac.getStatus())) {
						toDo.add(td);
					}
				}
			}
			System.out.println(ac.toString());
			break;
		}

		return outputString(output);
	}

	private static String outputString(String output) {

		if (!sch.isEmpty()) {
			output = "Schedules: \n";
			for (Schedule s : sch) {
				output = output + count + ". " + "Event: " + s.getDescription()
						+ "\n" + "Starting Time: " + s.getStartTime().getTime()
						+ "\n" + "Ending Time: " + s.getEndTime().getTime()
						+ "\n";

				if (!s.getPlace().equals("")) {
					output = output + "Place: " + s.getPlace() + "\n";
				}
				table.put(count, s);
				count++;
			}
			output = output + "\n";
		}

		if (!dl.isEmpty()) {
			output = output + "Deadlines: \n";
			for (Deadline d : dl) {
				output = output + count + ". " + "Event: " + d.getDescription()
						+ "\n" + "Due by: " + d.getEndTime().getTime() + "\n"
						+ "Status: " + d.getStatus() + "\n";

				if (!d.getPlace().equals("")) {
					output = output + "Place: " + d.getPlace() + "\n";
				}
				table.put(count, d);
				count++;
			}

			output = output + "\n";
		}

		if (!toDo.isEmpty()) {
			output = output + "Todos: \n";
			for (Todo td : toDo) {
				output = output + count + ". " + "Event: "
						+ td.getDescription() + "\n" + "Status: "
						+ td.getStatus() + "\n";

				if (!td.getPlace().equals("")) {
					output = output + "Place: " + td.getPlace() + "\n";
				}
				table.put(count, td);
				count++;
			}

			output = output + "\n";
		}

		return output;
	}

	private static String delete(DeleteAction ac) {

		try {
			BufferedWriter w = new BufferedWriter(new FileWriter(file));
			BufferedReader r = new BufferedReader(new FileReader(file));
			String currLine;

			for (Integer arr : ac.getReferenceNumber()) {
				Task t = table.get(arr);
				deletedTasks.add(t);

				while ((currLine = r.readLine()) != null) {
					if (!t.toString().equals(currLine)) {
						w.write("");
					}
				}

				if (t instanceof Schedule) {
					schedule.remove(t);
					sch.remove(t);
				}

				else if (t instanceof Deadline) {
					deadline.remove(t);
					dl.remove(t);
				}

				else if (t instanceof Todo) {
					todo.remove(t);
					toDo.remove(t);
				}

				w.close();
				r.close();

			}
		} catch (Exception e) {
			return "Delete was unsuccessful. Please try again \n";
		}

		return "Deleted " + ac.getReferenceNumber() + " successfully \n";

	}

	private static void deleteUndo() {
		for (Task t : deletedTasks) {
			if (t instanceof Schedule) {
				schedule.add((Schedule) t);
			} else if (t instanceof Deadline) {
				deadline.add((Deadline) t);
			} else if (t instanceof Todo) {
				todo.add((Todo) t);
			}
		}
	}

	private static String update(UpdateAction ac) {

		Task t = table.get(ac.getReferenceNumber());

		updateOriginalTask = t;

		String updatedTask;
		String originalTask;
		String currLine;

		try {
			BufferedWriter w = new BufferedWriter(new FileWriter(file));
			BufferedReader r = new BufferedReader(new FileReader(file));
			originalTask = t.toString();

			if (t instanceof Schedule) {
				((Schedule) t).setStartTime(ac.getUpdatedStartTime());
				((Schedule) t).setEndTime(ac.getUpdatedEndTime());
			}

			else if (t instanceof Deadline) {
				((Deadline) t).setEndTime(ac.getUpdatedEndTime());
			}

			t.setPlace(ac.getUpdatedLocationQuery());
			t.setDescription(ac.getUpdatedQuery());

			updatedTask = t.toString();

			while ((currLine = r.readLine()) != null) {
				if (currLine.equals(originalTask)) {
					w.write(updatedTask);
				}
			}

			w.close();
			r.close();
		}

		catch (Exception e) {
			return "Update was unsuccessful. Please try again";
		}

		return "Updated " + ac.getReferenceNumber() + " Successfully\n";
	}

	private static void updateUndo() {
		for (Schedule s : schedule) {
			if (s.getUniqueID() == updateOriginalTask.getUniqueID()) {
				s = (Schedule) updateOriginalTask;
			}
		}
		for (Deadline d : deadline) {
			if (d.getUniqueID() == updateOriginalTask.getUniqueID()) {
				d = (Deadline) updateOriginalTask;
			}
		}
		for (Todo td : todo) {
			if (td.getUniqueID() == updateOriginalTask.getUniqueID()) {
				td = (Todo) updateOriginalTask;
			}
		}
	}

	private static String search(SearchAction ac) {
		sch.clear();
		dl.clear();
		toDo.clear();
		table.clear();

		String output = new String("");
		count = 1;

		for (Schedule s : schedule) {
			if (s.getDescription().contains(ac.getQuery())) {
				if (s.getStartTime().after(ac.getStartTime())
						&& s.getEndTime().before(ac.getEndTime())) {
					sch.add(s);
				}
			}
		}

		for (Deadline d : deadline) {
			if (d.getDescription().contains(ac.getQuery())) {
				if (d.getEndTime().after(ac.getStartTime())
						&& d.getEndTime().before(ac.getEndTime())) {
					dl.add(d);
				}
			}
		}

		for (Todo td : todo) {
			if (td.getDescription().contains(ac.getQuery())) {
				toDo.add(td);
			}
		}

		output = "Search Matches: \n\n";
		
		if(todo.isEmpty() && deadline.isEmpty() && schedule.isEmpty()){
			output = output + "None";
		}

		return outputString(output);
	}

	private static String mark(MarkAction ac) {
		String numbers = "";
		markUndoList = new ArrayList<Task>();
		String originalTask;
		String updatedTask;
		String currLine;

		try {

			BufferedWriter w = new BufferedWriter(new FileWriter(file));
			BufferedReader r = new BufferedReader(new FileReader(file));

			for (Integer num : ac.getReferenceNumber()) {
				Task t = table.get(num);
				originalTask = t.toString();
				markUndoList.add(t);
				numbers = numbers + num + " ";

				if (t instanceof Deadline) {
					((Deadline) t).setStatus(ac.getStatus());
				}

				else if (t instanceof Todo) {
					((Todo) t).setStatus(ac.getStatus());
				}

				else {
					w.close();
					r.close();
					return "Mark Unsuccessful";
				}

				updatedTask = t.toString();

				while ((currLine = r.readLine()) != null) {
					if (currLine == originalTask) {
						w.write(updatedTask);
					}
				}

				w.close();
				r.close();
			}
		} catch (Exception e) {

		}

		return "Marked " + numbers + "as " + ac.getStatus();
	}

	public static void markUndo() {
		String status = ((MarkAction) lastAction).getStatus();
		String newStatus;

		assert status.equals("done") || status.equals("undone");

		if (status.equals("done")) {
			newStatus = "undone";
		} else {
			newStatus = "done";
		}
		for (Task t : markUndoList) {
			if (t instanceof Deadline) {
				((Deadline) t).setStatus(newStatus);
			} else {
				((Todo) t).setStatus(newStatus);
			}
		}
	}

	public static String executeCommand(AddAction ac) {
		Task t = classify(ac);
		lastAction = ac;
		System.out.println(ac.toString());
		return add(t);
	}

	public static String executeCommand(DisplayAction ac) {
		return display(ac);
	}

	public static String executeCommand(SearchAction ac) {
		System.out.println(ac.toString());
		return search(ac);
	}

	public static String executeCommand(DeleteAction ac) {
		lastAction = ac;
		return delete(ac);
	}

	public static String executeCommand(UpdateAction ac) {
		lastAction = ac;
		return update(ac);
	}

	public static String executeCommand(MarkAction ac) {
		lastAction = ac;
		return mark(ac);
	}

	public static String executeUndo(LocalAction ac) {
		if (ac instanceof AddAction) {
			addUndo();
		} else if (ac instanceof UpdateAction) {
			updateUndo();
		} else if (ac instanceof DeleteAction) {
			deleteUndo();
		} else if (ac instanceof MarkAction) {
			markUndo();
		}

		return "Undo Successful";
	}
}
