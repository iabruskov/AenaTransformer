package com.findPassengers.mvc.service;

import com.findPassengers.mvc.entity.*;
import com.findPassengers.mvc.repository.*;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;

@Service
public class TransformerService {

    @Autowired
    private Environment env;
    @Autowired
    private AenaflightSourceRepository aenaflightSourceRepository;
    @Autowired
    private AenaflightDestinationRepository aenaflightDestinationRepository;
    @Autowired
    private GroupsToTransformRepository groupsToTransformRepository;


    public void work() throws InterruptedException {
        List<GroupToTransform> groups;
        List<ProcessThread> threads = new ArrayList<>();
        Integer threadsSize = env.getProperty("threadsSize", Integer.TYPE, 1);

        do {
            groups = groupsToTransformRepository.findTop10000By();
            for(List<GroupToTransform> subListGroups: Lists.partition(new ArrayList<>(groups), groups.size()/threadsSize)) {
                ProcessThread thread = new ProcessThread(subListGroups);
                threads.add(thread);
                thread.start();
            }
            for(ProcessThread thread: threads){
                thread.join();
            }
        } while ( !groups.isEmpty() );
    }

    private class ProcessThread extends Thread implements Runnable {
        private List<GroupToTransform> groups;

        ProcessThread(List<GroupToTransform> groups){
            this.groups = groups;
        }

        @Override
        public void run() {
            for(GroupToTransform group: groups){
                List<Aenaflight2017> aenaflight2017List = aenaflightSourceRepository.findByFlightIcaoCodeAndFlightNumberAndSchdDepOnlyDateLtOrderByIdAsc(group.getFlight_icao_code(), group.getFlight_number(), group.getSchd_dep_only_date_lt());

                if(aenaflight2017List!=null && !aenaflight2017List.isEmpty()) {
                    AenaflightDestination aenaflightDestination = new AenaflightDestination();

                    Set<String> baggageInfoSet = new HashSet<>();
                    Set<String> counterSet = new HashSet<>();
                    Set<String> gateInfoSet = new HashSet<>();
                    Set<String> loungeInfoSet = new HashSet<>();
                    Set<String> terminalInfoSet = new HashSet<>();
                    Set<String> arrTerminalInfoSet = new HashSet<>();
                    for(Aenaflight2017 aenaflight2017: aenaflight2017List){
                        addToSet(baggageInfoSet,        aenaflight2017.getBaggage_info());
                        addToSet(counterSet,               aenaflight2017.getCounter());
                        addToSet(gateInfoSet,              aenaflight2017.getGate_info());
                        addToSet(loungeInfoSet,           aenaflight2017.getLounge_info());
                        addToSet(terminalInfoSet,         aenaflight2017.getTerminal_info());
                        addToSet(arrTerminalInfoSet,    aenaflight2017.getArr_terminal_info());
                    }
                    Aenaflight2017 aenaflight2017Last = aenaflight2017List.get(aenaflight2017List.size()-1);

                    aenaflightDestination.setAdep(aenaflight2017Last.getDep_apt_code_iata());
                    aenaflightDestination.setAdes(aenaflight2017Last.getArr_apt_code_iata());
                    aenaflightDestination.setFlight_code(aenaflight2017Last.getFlightIcaoCode());
                    aenaflightDestination.setFlight_number(aenaflight2017Last.getFlightNumber());
                    aenaflightDestination.setCarrier_code(aenaflight2017Last.getCarrier_icao_code());
                    aenaflightDestination.setCarrier_number(aenaflight2017Last.getCarrier_number());
                    aenaflightDestination.setStatus_info(aenaflight2017Last.getStatus_info());
                    aenaflightDestination.setSchd_dep_lt(toDate(aenaflight2017Last.getSchdDepOnlyDateLt(), aenaflight2017Last.getSchd_dep_only_time_lt()));
                    aenaflightDestination.setSchd_arr_lt(toDate(aenaflight2017Last.getSchd_arr_only_date_lt(), aenaflight2017Last.getSchd_arr_only_time_lt()));
                    aenaflightDestination.setEst_dep_lt(toDate(aenaflight2017Last.getEst_dep_date_time_lt()));
                    aenaflightDestination.setEst_arr_lt(toDate(aenaflight2017Last.getEst_arr_date_time_lt()));
                    aenaflightDestination.setAct_dep_lt(toDate(aenaflight2017Last.getAct_dep_date_time_lt()));
                    aenaflightDestination.setAct_arr_lt(toDate(aenaflight2017Last.getAct_arr_date_time_lt()));
                    aenaflightDestination.setFlt_leg_seq_no(Integer.valueOf(aenaflight2017Last.getFlt_leg_seq_no()));
                    aenaflightDestination.setAircraft_name_scheduled(aenaflight2017Last.getAircraft_name_scheduled());
                    aenaflightDestination.setBaggage_info(reverseJoin(baggageInfoSet));
                    aenaflightDestination.setCounter(reverseJoin(counterSet));
                    aenaflightDestination.setGate_info(reverseJoin(gateInfoSet));
                    aenaflightDestination.setLounge_info(reverseJoin(loungeInfoSet));
                    aenaflightDestination.setTerminal_info(reverseJoin(terminalInfoSet));
                    aenaflightDestination.setArr_terminal_info(reverseJoin(arrTerminalInfoSet));
                    aenaflightDestination.setSource_data(aenaflight2017Last.getSource_data());
                    aenaflightDestination.setCreated_at(toDate(aenaflight2017Last.getCreated_at()));

                    try {
                        aenaflightDestinationRepository.saveAndFlush(aenaflightDestination);
                    } catch(Exception ex){
                        ex.printStackTrace();
//                        break;
                    }
                }
            }
        }

        private void addToSet(Set<String> set, String val){
            if(!Strings.isNullOrEmpty(val) && !"-".equals(val)){
                set.add(val);
            }
        }

    }


    private Date toDate(Long date){
        if(date !=null) {
            return new Date(date * 1000);
        } else {
            return null;
        }
    }

    private Date toDate(String date, String time){
        return toDate(date + " " + time);
    }

    private Date toDate(String date){
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yy hh:mm");
        try {
            return formatter.parse(date);
        } catch (ParseException e) {
            return null;
        }
    }

    private String reverseJoin(Set<String> set){
        StringBuilder result = new StringBuilder();
        for(String baggage: set ) {
            if(result.length() >0 ){
                result.insert(0, ",");
            }
            result.insert(0, baggage);
        }
        return result.toString();
    }

}
