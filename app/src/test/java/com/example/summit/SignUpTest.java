package com.example.summit;

import com.example.summit.model.Entrant;
import com.example.summit.model.Firebase;
import com.example.summit.model.SignUp;
import com.example.summit.model.WaitingList;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.mockito.quality.Strictness.LENIENT;

/**
 * Unit tests for the SignUp class.
 * This class uses Mockito to mock all external dependencies, including static
 * methods in the Firebase class and the FirebaseFirestore SDK.
 * These tests do not require the Firebase Emulator to be running.
 */
@ExtendWith(MockitoExtension.class)
class SignUpTest {
    private SignUp signUp;

    // --- Mocks ---
    @Mock
    private Entrant mockEntrant;

    @Mock
    private WaitingList mockList;

    // These will be used to mock the static methods
    private MockedStatic<Firebase> mockedFirebase;
    private MockedStatic<FirebaseFirestore> mockedFirestore;

    @BeforeEach
    void setUp() {
        signUp = new SignUp();

        mockedFirebase = mockStatic(Firebase.class, withSettings().strictness(LENIENT));
        mockedFirestore = mockStatic(FirebaseFirestore.class, withSettings().strictness(LENIENT));
    }

    @AfterEach
    void tearDown() {
        mockedFirebase.close();
        mockedFirestore.close();
    }

    @Test
    void joinWaitingList_WhenCalled_InteractsWithDependenciesCorrectly() {
        signUp.joinWaitingList(mockEntrant, mockList);

        verify(mockList, times(1)).addEntrant(mockEntrant);

        mockedFirebase.verify(
                () -> Firebase.saveEntrant(mockEntrant),
                times(1)
        );

        mockedFirebase.verify(
                () -> Firebase.updateWaitingList(mockList),
                times(1)
        );
    }

    @Test
    void leaveWaitingList_WhenCalled_RemovesEntrantTwice() {
        signUp.leaveWaitingList(mockEntrant, mockList);

        verify(mockList, times(2)).removeEntrant(mockEntrant);

        mockedFirebase.verify(
                () -> Firebase.updateWaitingList(mockList),
                times(1)
        );
    }

    @Test
    void acceptInvitation_WhenCalled_DelegatesToEntrant() {
        signUp.acceptInvitation(mockEntrant);

        verify(mockEntrant, times(1)).acceptInvitation();
    }

    @Test
    void joinEvent_EntrantNotOnList_AddsEntrantAndReturnsTrue() {
        ArrayList<Entrant> emptyList = new ArrayList<>();

        when(mockList.getEntrants()).thenReturn(emptyList);

        boolean result = signUp.joinEvent(mockEntrant, mockList);

        assertTrue(result, "Should return true when entrant is added");
        verify(mockList, times(1)).addEntrant(mockEntrant);
    }

    @Test
    void joinEvent_EntrantAlreadyOnList_DoesNotAddAndReturnsFalse() {
        ArrayList<Entrant> listWithEntrant = new ArrayList<>();
        listWithEntrant.add(mockEntrant);
        when(mockList.getEntrants()).thenReturn(listWithEntrant);

        boolean result = signUp.joinEvent(mockEntrant, mockList);

        assertFalse(result, "Should return false when entrant is already on list");

        verify(mockList, never()).addEntrant(any(Entrant.class));
    }

    @Test
    void joinEventFirestore_WhenCalled_BuildsCorrectFirestoreQuery() {
        String testEventId = "event-123";
        String testEntrantId = "entrant-abc";

        when(mockEntrant.getDeviceId()).thenReturn(testEntrantId);

        FirebaseFirestore mockDb = mock(FirebaseFirestore.class);
        CollectionReference mockEventsCol = mock(CollectionReference.class);
        DocumentReference mockEventDoc = mock(DocumentReference.class);
        CollectionReference mockWaitingListCol = mock(CollectionReference.class);
        DocumentReference mockEntrantDoc = mock(DocumentReference.class);
        Task<Void> mockTask = mock(Task.class);

        mockedFirestore.when(FirebaseFirestore::getInstance).thenReturn(mockDb);

        when(mockDb.collection("events")).thenReturn(mockEventsCol);
        when(mockEventsCol.document(testEventId)).thenReturn(mockEventDoc);
        when(mockEventDoc.collection("waitingList")).thenReturn(mockWaitingListCol);
        when(mockWaitingListCol.document(testEntrantId)).thenReturn(mockEntrantDoc);
        when(mockEntrantDoc.set(mockEntrant)).thenReturn(mockTask);

        when(mockTask.addOnSuccessListener(any())).thenReturn(mockTask);
        when(mockTask.addOnFailureListener(any())).thenReturn(mockTask);

        signUp.joinEventFirestore(mockEntrant, testEventId);

        mockedFirestore.verify(FirebaseFirestore::getInstance);
        verify(mockDb).collection("events");
        verify(mockEventsCol).document(testEventId);
        verify(mockEventDoc).collection("waitingList");
        verify(mockWaitingListCol).document(testEntrantId);
        verify(mockEntrantDoc).set(mockEntrant); // Verifies the correct object was passed
    }

    @Test
    void leaveEvent_EntrantOnList_RemovesEntrantAndReturnsTrue() {
        ArrayList<Entrant> listWithEntrant = new ArrayList<>();
        listWithEntrant.add(mockEntrant);
        when(mockList.getEntrants()).thenReturn(listWithEntrant);

        boolean result = signUp.leaveEvent(mockEntrant, mockList);

        assertTrue(result, "Should return true when entrant is removed");
        verify(mockList, times(1)).removeEntrant(mockEntrant);
    }

    @Test
    void leaveEvent_EntrantNotOnList_DoesNotRemoveAndReturnsFalse() {
        ArrayList<Entrant> emptyList = new ArrayList<>();
        when(mockList.getEntrants()).thenReturn(emptyList);

        boolean result = signUp.leaveEvent(mockEntrant, mockList);

        assertFalse(result, "Should return false when entrant is not on list");
        verify(mockList, never()).removeEntrant(any(Entrant.class));
    }
}