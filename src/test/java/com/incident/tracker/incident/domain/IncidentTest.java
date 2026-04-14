package com.incident.tracker.incident.domain;

import com.incident.tracker.incident.domain.exception.IncidentAlreadyClosedException;
import com.incident.tracker.incident.domain.model.Incident;
import com.incident.tracker.incident.domain.model.IncidentStatus;
import com.incident.tracker.incident.domain.model.Priority;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class IncidentTest {
    @Test
    void should_create_incident_with_open_status() {
        Incident incident = new Incident("title", "desc", Priority.P2);

        assertThat(incident.getTitle()).isEqualTo("title");
        assertThat(incident.getIncidentStatus()).isEqualTo(IncidentStatus.OPEN);
    }

    @Test
    void should_throw_exception_when_title_is_blank() {
        assertThatThrownBy(() -> new Incident("", "desc", Priority.P2))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_close_incident() {
        Incident incident = new Incident("title", "desc", Priority.P2);

        incident.close();

        assertThat(incident.getIncidentStatus()).isEqualTo(IncidentStatus.CLOSED);
    }

    @Test
    void should_not_close_already_closed_incident() {
        Incident incident = new Incident("title", "desc", Priority.P2);

        incident.close();
        assertThatThrownBy(incident::close).isInstanceOf(IncidentAlreadyClosedException.class);
    }

    @Test
    void should_update_incident() {
        Incident incident = new Incident("title", "desc", Priority.P2);

        incident.update("new title", "new desc", Priority.P4.label, IncidentStatus.IN_PROGRESS.name());

        assertThat(incident.getTitle()).isEqualTo("new title");
        assertThat(incident.getDescription()).isEqualTo("new desc");
        assertThat(incident.getPriority()).isEqualTo(Priority.P4);
    }

    @Test
    void should_not_update_closed_incident() {
        Incident incident = new Incident("title", "desc", Priority.P2);

        incident.close();
        assertThatThrownBy(() -> incident.update("new", "new", Priority.P4.label, IncidentStatus.CLOSED.label))
                .isInstanceOf(IncidentAlreadyClosedException.class);
    }

    @Test
    void should_add_comment() {
        Incident incident = new Incident("title", "desc", Priority.P2);

        incident.addComment("comment", "author");

        assertThat(incident.getComments()).hasSize(1);
        assertThat(incident.getComments().getFirst().getContent()).isEqualTo("comment");
    }

    @Test
    void should_not_add_comment_if_incident_closed() {
        Incident incident = new Incident("title", "desc", Priority.P2);

        incident.close();
        assertThatThrownBy(() -> incident.addComment("comment", "author"))
                .isInstanceOf(IllegalStateException.class);
    }
}
