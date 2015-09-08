package org.commonjava.maven.atlas.graph.mutate;


public class GraphMutatorSelector
{

    /**
     * Selects mutator based on the input value. When the input value is {@code null} then it chooses the default
     * mutator.
     * 
     * @param value
     *            the identifier of the mutator type
     * @return the selected mutator
     * @throws IllegalArgumentException
     *             if the input value is not a String instance or a String[] instance or if the value does not match any
     *             mutator type
     */
    public static GraphMutator selectMutator( final Object value )
    {
        String mutatorCode = null;
        if ( value != null )
        {
            if ( value instanceof String )
            {
                mutatorCode = (String) value;
            }
            else if ( value instanceof String[] )
            {
                String[] strings = (String[]) value;

                if ( strings.length == 1 )
                {
                    mutatorCode = strings[0];
                }
                else if ( strings.length > 1 )
                {
                    throw new IllegalArgumentException( "More than 1 value was passed for selection of mutator: "
                            + strings );
                }
            }
        }

        GraphMutator mutator;
        MutatorType mt;
        if ( mutatorCode == null )
        {
            mt = MutatorType.getDefault();
        }
        else
        {
            mt = MutatorType.valueOf( mutatorCode );
        }

        if ( mt == MutatorType.NOP )
        {
            mutator = NoOpGraphMutator.INSTANCE;
        }
        else if ( mt == MutatorType.MANAGED_DEPENDENCY )
        {
            mutator = new ManagedDependencyMutator();
        }
        else
        {
            throw new IllegalArgumentException( "The mutator type " + mt + " is not yet supported by this factory." ); 
        }
        return mutator;
    }

    /**
     * Enum of mutator types supported by this Faktory.
     */
    private enum MutatorType
    {
        /** Value for {@link NoOpGraphMutator}. */
        NOP,
        /** Value for {@link ManagedDependencyMutator}. */
        MANAGED_DEPENDENCY;

        /**
         * @return the default mutator type
         */
        private static MutatorType getDefault()
        {
            return MANAGED_DEPENDENCY;
        }
    }

}
