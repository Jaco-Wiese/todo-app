package org.example.mother;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;
import org.example.model.Todo;

@With
@AllArgsConstructor
@NoArgsConstructor
public class TodoMother {
	private String title = "Bake cookies";
	private String description = "We need to bake cookies for the party tonight";
	private boolean completed = false;

	public Todo build() {
		return new Todo(
			title,
			description,
			completed
		);
	}

}
