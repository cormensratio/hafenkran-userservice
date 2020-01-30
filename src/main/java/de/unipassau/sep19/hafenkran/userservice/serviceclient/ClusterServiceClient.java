package de.unipassau.sep19.hafenkran.userservice.serviceclient;

import lombok.NonNull;

import java.util.UUID;

/**
 * A service for communicating with the ClusterService.
 */
public interface ClusterServiceClient {

    void deleteExperimentsAndExecutionsFromUser(@NonNull UUID ownerId);

}
