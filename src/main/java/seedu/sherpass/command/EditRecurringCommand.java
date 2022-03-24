package seedu.sherpass.command;

import seedu.sherpass.enums.Frequency;
import seedu.sherpass.task.Task;
import seedu.sherpass.task.TaskList;
import seedu.sherpass.util.CommonLogic;
import seedu.sherpass.util.Storage;
import seedu.sherpass.util.Ui;

import java.time.LocalDateTime;

import static seedu.sherpass.constant.Message.EMPTY_STRING;
import static seedu.sherpass.constant.Message.ERROR_EMPTY_DESCRIPTION_MESSAGE;
import static seedu.sherpass.constant.Message.ERROR_EMPTY_TASKLIST_MESSAGE;
import static seedu.sherpass.constant.Message.ERROR_INVALID_INDEX_MESSAGE;
import static seedu.sherpass.constant.Message.ERROR_START_AFTER_END_TIME_MESSAGE;

public class EditRecurringCommand extends Command {
    private int index;
    private String taskDescription;
    private LocalDateTime doOnStartDateTime;
    private LocalDateTime doOnEndDateTime;

    public EditRecurringCommand() {

    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }

    public void setDoOnStartDateTime(LocalDateTime doOnStartDateTime) {
        this.doOnStartDateTime = doOnStartDateTime;
    }

    public void setDoOnEndDateTime(LocalDateTime doOnEndDateTime) {
        this.doOnEndDateTime = doOnEndDateTime;
    }

    private String isValidArgument(TaskList taskList) {
        if (taskDescription.isBlank()) {
            return ERROR_EMPTY_DESCRIPTION_MESSAGE;
        } else if (doOnStartDateTime != null && doOnStartDateTime.isAfter(doOnEndDateTime)) {
            return ERROR_START_AFTER_END_TIME_MESSAGE;
        } else if (taskList.getTasks().size() == 0) {
            return ERROR_EMPTY_TASKLIST_MESSAGE;
        } else if (!taskList.isTaskExist(index)) {
            return ERROR_INVALID_INDEX_MESSAGE;
        }
        return EMPTY_STRING;
    }

    @Override
    public void execute(TaskList taskList, Ui ui, Storage storage) {
        String error = isValidArgument(taskList);
        if (!error.isBlank()) {
            ui.showToUser(error);
            return;
        }

        Frequency repeatFrequency = taskList.getTasks().get(index).getRepeatFrequency();
        int oldIdentifier = taskList.getTasks().get(index).getIdentifier();
        int newIdentifier = taskList.generateIdentifier();
        StringBuilder editedTaskString = new StringBuilder();

        for (int i = index; i < taskList.getTasks().size(); i++) {
            Task t = taskList.getTasks().get(i);
            if (t.getIdentifier() == oldIdentifier) {
                if (!taskDescription.isBlank()) {
                    t.setTaskDescription(taskDescription);
                }
                if (doOnStartDateTime != null) {
                    t.setDoOnStartDateTime(CommonLogic.incrementDate(doOnStartDateTime, repeatFrequency));
                    t.setDoOnEndDateTime(CommonLogic.incrementDate(doOnEndDateTime, repeatFrequency));
                }
                t.setIdentifier(newIdentifier);
                editedTaskString.append(t);
                editedTaskString.append("\n ");
            }
        }
        ui.printEditTaskMessage(editedTaskString.toString());
        storage.writeSaveData(taskList);
    }
}
