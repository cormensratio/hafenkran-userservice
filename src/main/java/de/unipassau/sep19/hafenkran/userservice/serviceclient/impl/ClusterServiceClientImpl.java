package de.unipassau.sep19.hafenkran.userservice.serviceclient.impl;

import de.unipassau.sep19.hafenkran.userservice.serviceclient.ClusterServiceClient;
import de.unipassau.sep19.hafenkran.userservice.serviceclient.ServiceClient;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * {@inheritDoc}
 */
@Service
@RequiredArgsConstructor(onConstructor = @__({@Autowired}))
public class ClusterServiceClientImpl implements ClusterServiceClient {

    private final ServiceClient serviceClient;

    @Value("${cluster-service-uri}")
    private String basePath;

    /**
     * {@inheritDoc}
     */
    public void deleteExperimentsAndExecutionsFromUser(@NonNull UUID ownerId) {
        serviceClient.post(basePath + "/experiments/deleteExperiments?ownerId=" + ownerId, "", null);
    }

}
