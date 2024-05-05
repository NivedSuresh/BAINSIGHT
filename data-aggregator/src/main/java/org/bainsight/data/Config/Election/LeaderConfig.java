package org.bainsight.data.Config.Election;

import com.hazelcast.cluster.MembershipEvent;
import com.hazelcast.cluster.MembershipListener;
import com.hazelcast.config.Config;
import com.hazelcast.core.HazelcastInstance;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.atomic.AtomicBoolean;


@Configuration
@Slf4j
public class LeaderConfig implements MembershipListener {

    private HazelcastInstance hazelcast;
    private String leaderAddress;

    public final static AtomicBoolean IS_LEADER;

    static
    {
        IS_LEADER = new AtomicBoolean(false);
    }

    public LeaderConfig(HazelcastInstance instance)
    {
        this.hazelcast = instance;
        this.onStart();
    }


    private void onStart() {

        log.info("Total members in the cluster: {}", hazelcast.getCluster().getMembers().size());
        hazelcast.getCluster().addMembershipListener(this);
        leaderAddress = getLeaderAddress();
        log.info("Current leader address: " + leaderAddress);

        checkIfElected();
    }

    @Override
    public void memberAdded(MembershipEvent membershipEvent) {
        if (membershipEvent.getMember().getSocketAddress().toString().compareTo(leaderAddress) < 0) {
            leaderAddress = membershipEvent.getMember().getSocketAddress().toString();
            checkIfElected();
        }
    }

    @Override
    public void memberRemoved(MembershipEvent membershipEvent) {
        if (membershipEvent.getMember().getSocketAddress().toString().equals(leaderAddress)) {
            leaderAddress = getLeaderAddress();
            checkIfElected();
        }
    }

    private void checkIfElected(){
        if(hazelcast.getCluster().getLocalMember().getSocketAddress().toString().equals(leaderAddress))
        {
            IS_LEADER .set(true);
            log.info("This node has been elected as the new leader!");
        }
    }

    private String getLeaderAddress(){
        return hazelcast.getCluster().getMembers().iterator().next().getSocketAddress().toString();
    }
}
