/*
 * Copyright (c) 2026. Arquitectura de Sistemas, DISC, UCN, Antofagasta.
 */

package cl.ucn.disc.arqsist.library;

import cl.ucn.disc.arqsist.library.dao.MemberDao;
import cl.ucn.disc.arqsist.library.db.Database;
import cl.ucn.disc.arqsist.library.grpc.MemberClient;
import cl.ucn.disc.arqsist.library.grpc.MemberGrpcService;
import cl.ucn.disc.arqsist.library.grpc.MemberServiceGrpc;
import cl.ucn.disc.arqsist.library.grpc.SearchMembersRequest;
import cl.ucn.disc.arqsist.library.model.Member;
import cl.ucn.disc.arqsist.library.service.MemberService;
import io.grpc.ManagedChannel;
import io.grpc.Server;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests member search through the in-process gRPC transport.
 */
final class MemberSearchTest {

    @Test
    void searchesMembersAndRejectsBlankQueries() throws Exception {
        Database db = new Database("jdbc:sqlite::memory:");
        MemberService service = new MemberService(new MemberDao(db.connectionSource()));
        service.register(new Member("Ada Lovelace", "ada@example.com"));
        service.register(new Member("Grace Hopper", "grace@example.com"));
        service.register(new Member("Alan Turing", "alan@example.com"));

        String serverName = InProcessServerBuilder.generateName();
        Server server = InProcessServerBuilder.forName(serverName).directExecutor()
                .addService(new MemberGrpcService(service))
                .build()
                .start();
        try {
            ManagedChannel channel = InProcessChannelBuilder.forName(serverName).directExecutor().build();
            try (MemberClient client = new MemberClient(channel)) {
                List<String> ada = List.of("Ada Lovelace");
                assertEquals(ada, names(client.search("ada")));
                assertEquals(ada, names(client.search("ADA")));
                assertEquals(ada, names(client.search(" ada ")));
                assertEquals(List.of("Ada Lovelace", "Grace Hopper", "Alan Turing"), names(client.search("a")));
                assertTrue(client.search("zzz").isEmpty());
                assertTrue(client.search("%").isEmpty());
                assertTrue(client.search("_").isEmpty());

                var stub = MemberServiceGrpc.newBlockingStub(channel);
                for (String blank : List.of("", " ")) {
                    SearchMembersRequest request = SearchMembersRequest.newBuilder().setQuery(blank).build();
                    StatusRuntimeException error = assertThrows(StatusRuntimeException.class,
                            () -> stub.withDeadlineAfter(5, TimeUnit.SECONDS).searchMembers(request));
                    assertEquals(Status.Code.INVALID_ARGUMENT, error.getStatus().getCode());
                    assertThrows(IllegalArgumentException.class, () -> client.search(blank));
                }
            }
        } finally {
            server.shutdownNow();
            db.connectionSource().close();
        }
    }

    private static List<String> names(List<Member> members) {
        return members.stream().map(Member::getName).toList();
    }
}
