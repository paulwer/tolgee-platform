import { Checkbox, FormControlLabel, styled } from '@mui/material';
import {
  HierarchyItem,
  HierarchyType,
  PermissionModelScope,
} from 'tg.component/PermissionsSettings/types';
import {
  checkChildren,
  getChildScopes,
  getDependent,
  getRequiredScopes,
} from './tools';
import { useScopeTranslations } from './useScopeTranslations';

const StyledContainer = styled('div')`
  display: grid;
`;

const StyledChildren = styled('div')`
  display: grid;
  margin-left: 32px;
`;

type Props = {
  dependencies: HierarchyItem;
  structure: HierarchyType;
  scopes: PermissionModelScope[];
  setScopes: (scopes: PermissionModelScope[], value: boolean) => void;
};

export const Hierarchy: React.FC<Props> = ({
  dependencies,
  structure,
  scopes,
  setScopes,
}) => {
  const scopeIncluded = structure.value && scopes.includes(structure.value);
  const { childrenCheckedSome, childrenCheckedAll } = checkChildren(
    structure,
    scopes
  );

  // scopes the item is responsible for
  const myScopes = structure.value
    ? [structure.value]
    : getChildScopes(structure);

  // scopes which are dependent on myScopes
  const dependentScopes = getDependent(myScopes, dependencies);

  // check if all dependant scopes are in responsible nodes
  // meaning if we toggle this, nothing outside gets broken
  const blockingScopes = dependentScopes.filter(
    (dependentScope) =>
      scopes.includes(dependentScope) && !myScopes.includes(dependentScope)
  );

  const disabled = Boolean(blockingScopes.length);

  const fullyChecked =
    scopeIncluded || (!structure.value && childrenCheckedAll);
  const halfChecked = !structure.value && !fullyChecked && childrenCheckedSome;

  const handleClick = () => {
    if (fullyChecked) {
      setScopes(myScopes, false);
    } else {
      // get myScopes and also their required scopes
      const influencedScopes = getRequiredScopes(myScopes, dependencies);
      setScopes(influencedScopes, true);
    }
  };

  const { getScopeTranslation } = useScopeTranslations();

  const label =
    structure.label ||
    (structure.value ? getScopeTranslation(structure.value) : undefined);

  return (
    <StyledContainer>
      <FormControlLabel
        control={
          <Checkbox
            checked={fullyChecked}
            indeterminate={halfChecked}
            onClick={handleClick}
            disabled={disabled}
          />
        }
        label={label}
      />
      <StyledChildren>
        {structure.children?.map((child) => {
          return (
            <Hierarchy
              key={child.value}
              dependencies={dependencies}
              structure={child}
              scopes={scopes}
              setScopes={setScopes}
            />
          );
        })}
      </StyledChildren>
    </StyledContainer>
  );
};